package network;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;

/**
 * Classe permettant d'instancier puis de gérer un nouveau client.
 * @author Barry Hezam
 */
public class Client
{
    private static final Logger log = LoggerFactory.getLogger(Client.class);

    private static final String serverAddress = "http://127.0.0.1:6666";

    private Socket socket;

    /**
     * Méthode permettant d'initialiser le client
     */
    public void init()
    {
        try {
            this.socket = IO.socket(serverAddress);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        this.socket.on("connect", objects -> {
            log.info("Connecté au serveur.");
        });
        this.socket.on("connect_failed", objects -> {
            log.warn("Une erreur est survenue lors de la connexion au serveur.");
        });
        this.socket.on("disconnect", objects -> {
            log.info(String.format("Déconnecté du serveur (%s).", objects[0].toString()));
        });
    }

    /**
     * Méthode permettant de se connecter au serveur
     */
    public void connect()
    {
        if(!this.socket.connected())
        {
            log.info("Tentative de connexion au serveur...");
            this.socket.connect();
        }
    }

    /**
     * Méthode permettant de se déconnecter du serveur
     */
    public void disconnect()
    {
        if(this.socket.connected())
            this.socket.disconnect();
    }

    /**
     * Méthode permettant d'enregister un listener depuis une classe extérieure
     * @param eventName nom de l'événement
     * @param listener écouteur
     */
    public void registerHandler(String eventName, Emitter.Listener listener)
    {
        this.socket.on(eventName, listener);
    }

    /**
     * Méthode permettant d'obtenir l'UUID du Client.
     * @return UUID du client sous forme de chaîne de caractères.
     */
    public String getUUID(){return socket.id();}
}