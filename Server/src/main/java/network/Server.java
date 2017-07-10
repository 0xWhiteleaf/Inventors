package network;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DefaultExceptionListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import engine.Game;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Classe permettant de gérer un serveur.
 * @author Alexandre Ciarafoni, Noé Mourton-Comte
 */
public class Server implements ConnectListener, DisconnectListener
{
    private static final Logger log = LoggerFactory.getLogger(Server.class);

    private final String hostname = "127.0.0.1";
    private final int port = 6666;

    private SocketIOServer server;
    private Configuration config;
    private boolean isListening;

    private ArrayDeque<SocketIOClient> waitingClients;

    private ArrayList<ServerListener> listeners;

    public Server()
    {
        //Initialisation de la liste des écouteurs...
        this.listeners = new ArrayList<>();

        //Initialisation de la configuration du serveur...
        this.config = new Configuration();
        config.setHostname(this.hostname);
        config.setPort(this.port);

        this.init();
    }

    /**
     * Méthode permettant d'initialiser le serveur
     */
    private void init()
    {
        //Initialisation du serveur...
        this.server = new SocketIOServer(this.config);

        this.waitingClients = new ArrayDeque<>();
        this.isListening = false;

        this.server.addConnectListener(this);
        this.server.addDisconnectListener(this);
    }

    /**
     * Méthode permettant de mettre le serveur en écoute
     */
    public void start()
    {
        if(!this.isListening)
        {
            //Démarrage du serveur
            this.server.start();
            this.isListening = true;
        }
    }

    /**
     * Méthode permettant d'arrêter l'écoute du serveur
     */
    public void stop()
    {
        if(this.isListening)
        {
            //Arrêt du serveur
            this.server.stop();
            this.isListening = false;
        }
    }

    //region EventHandlers

    /**
     * Écouteur appelé lors de l'arrivée d'un nouveau client
     */
    @Override
    public void onConnect(SocketIOClient socketIOClient)
    {
        synchronized (this)
        {
            log.info(String.format("Un nouveau client est connecté (%s).", socketIOClient.getRemoteAddress()));

            this.waitingClients.add(socketIOClient);
            socketIOClient.sendEvent(ProtocolMessages.WelcomeMessage.toString(), "");
            log.info("Inscription du client pour la prochaine partie terminée.");

            if (this.waitingClients.size() >= Game.REQUIRED_PLAYERS) {
                ArrayList<SocketIOClient> gamePlayers = new ArrayList<>();

                for (int i = 0; i < Game.REQUIRED_PLAYERS; i++) {
                    gamePlayers.add(this.waitingClients.remove());
                }

                this.onGameReady(gamePlayers);
            }
        }
    }

    /**
     * Écouteur appelé lors de la déconnexion d'un client
     */
    @Override
    public void onDisconnect(SocketIOClient socketIOClient)
    {
        log.info(String.format("Un client vient de se déconnecter (%s).", socketIOClient.getRemoteAddress()));
        //TODO: Terminer la partie si le client est joueur dans une partie...
    }

    //endregion

    //region Listeners

    public void addListener(ServerListener listener)
    {
        this.listeners.add(listener);
    }

    private void onGameReady(List<SocketIOClient> players)
    {
        for(ServerListener listener : this.listeners)
        {
            listener.onGameReady(players);
        }
    }

    //endregion
}
