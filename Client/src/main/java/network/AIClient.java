package network;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import engine.ai.AIPlayer;
import engine.ai.strategies.Strategy;
import engine.ai.strategies.providers.InventionProvider;
import engine.ai.strategies.providers.UUIDProvider;
import engine.core.Invention;
import engine.core.Inventor;
import engine.core.Player;
import engine.core.rewards.Reward;
import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import network.listener.AIClientListener;
import network.listener.InjectableListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Classe permettant de gérer les évenements envoyés par le serveur et liés à l'IA.
 * @author Sappa Valentin & Louis Prud'homme & Alexandre Ciarafoni
 */
public class AIClient extends Client implements InventionProvider, UUIDProvider
{
    private static final Logger log = LoggerFactory.getLogger(AIClient.class);

    private ArrayList<AIClientListener> listeners;

    private AIPlayer aiPlayer;
    private Class<?> strategyType;
    private boolean displayLog;

    private HashMap<UUID, List<Inventor>> otherPlayers;
    private ArrayList<Invention> availableInventions;

    private Gson gsonSerializer = new Gson();

    public AIClient(Class<?> strategyType, boolean displayLog) throws URISyntaxException
    {
        super.init();

        this.listeners = new ArrayList<>();

        this.aiPlayer = new AIPlayer();
        this.strategyType = strategyType;
        this.displayLog = displayLog;

        this.otherPlayers = new HashMap<>();
        this.availableInventions = new ArrayList<>();

        this.registerHandlers();
    }

    /**
     * Permet d'enregistrer les différents écouteurs auprès du client cible.
     */
    public void registerHandlers()
    {
        //Enregistrement des écouteurs
        super.registerHandler(ProtocolMessages.GameDeniedMessage.name(), gameDeniedMessageListener);
        super.registerHandler(ProtocolMessages.WelcomeMessage.name(), welcomeMessageListener);
        super.registerHandler(ProtocolMessages.GameStartedMessage.name(), gameStartedMessageListener(this, this.aiPlayer));
        super.registerHandler(ProtocolMessages.TurnStartedMessage.name(), turnStartedMessageListener(this, this.aiPlayer, false));
        super.registerHandler(ProtocolMessages.SynchronizeMessage.name(), synchronizeMessageListener(this, this.aiPlayer));
        super.registerHandler(ProtocolMessages.GameEndedMessage.name(), gameEndedMessageListener);
        super.registerHandler(ProtocolMessages.RewardMessage.name(), rewardMessageListener(this, this.aiPlayer));
        super.registerHandler(ProtocolMessages.LogMessage.name(), logMessageListener);
        super.registerHandler(Socket.EVENT_DISCONNECT, disconnectListener);
    }

    /**
     * Ecouteur du message GameDenied, signifiant le refus de l'inscirption du joueur à la prochaine partie.
     */
    public Emitter.Listener gameDeniedMessageListener = objects ->
    {
        log.info("Votre inscription pour la prochaine partie n'a pas pu être effectuée.");
        log.warn("Vous allez être deconnecté...");
    };

    /**
     * Ecouteur du message Welcome, signifiant l'inscirption du joueur à la prochaine partie.
     */
    public Emitter.Listener welcomeMessageListener = objects ->
    {
        if(displayLog)
        {
            log.info("Votre inscription pour la prochaine partie vient d'être confirmée !");
            log.info("Vous êtes maintenant en attente du début de la partie...");
        }
    };

    /**
     * Ecouteur du message RewardMessage
     */
    public InjectableListener rewardMessageListener(AIClient aiClient, AIPlayer aiPlayer)
    {
        return new InjectableListener(aiClient, aiPlayer)
        {
            @Override
            public void call(Object... objects)
            {
                String rewards = "";
                ArrayList<Reward> rewardList = gsonSerializer.fromJson(objects[0].toString(), new TypeToken<ArrayList<Reward>>(){}.getType());

                int i = 1;
                for (Reward r : rewardList)
                {
                    if (rewardList.size() == i)
                    {
                        rewards = rewards + r.toString() + ".";
                    }
                    else
                    {
                        rewards = rewards + r.toString() + ", ";
                    }
                    i++;
                }

                if(displayLog)
                    log.info("Récompenses disponibles : " + rewards);

                Ack ack = (Ack)objects[objects.length - 1];

                int selectedReward = aiPlayer.requestReward(rewardList);
                ack.call(selectedReward);

                if(displayLog)
                    log.info("Vous avez choisi la récompense : " + rewardList.get(selectedReward).toString());

            }
        };
    }

    /**
     * Ecouteur du message GameStarted, qui signifie au joueur que la partie commence et lui envoie les informations nécéssaires, telles que les inventions jouées, ses inventeurs et les inventeurs des autres joueurs.
     */
    public InjectableListener gameStartedMessageListener(AIClient aiClient, AIPlayer aiPlayer)
    {
        return new InjectableListener(aiClient, aiPlayer)
        {
            @Override
            public void call(Object... objects)
            {
                log.info("Début de la partie !");

                //Initialisation de l'intelligence artificielle...
                try
                {
                    aiClient.init(aiClient, aiPlayer);
                }
                catch (Exception e)
                {
                    aiClient.disconnect();
                    log.error(e.getMessage());
                }

                //Désérialisation du json...
                aiClient.otherPlayers = gsonSerializer.fromJson((String) objects[0],
                        new TypeToken<HashMap<UUID, List<Inventor>>>() {}.getType());

                aiClient.availableInventions = gsonSerializer.fromJson((String)objects[1], new TypeToken<ArrayList<Invention>>(){}.getType());

                //Tri des inventeurs du joueur et rfraichissmeent avant suppression de la liste, uniquement consacrée à cataloguer les inventeurs des autres joueurs.
                aiPlayer.refreshInventors(aiClient.otherPlayers.get(UUID.fromString(aiClient.getUUID())));
                aiClient.otherPlayers.remove(UUID.fromString(aiClient.getUUID()));
            }
        };
    }

    /**
     * Ecouteur du message TurnStarted, qui signifie au joueur que c'est à son tour de jouer
     */
    public InjectableListener turnStartedMessageListener(AIClient aiClient, AIPlayer aiPlayer, boolean isInjected)
    {
        return new InjectableListener(aiClient, aiPlayer, isInjected)
        {
            @Override
            public void call(Object... objects)
            {
                Ack ack = (Ack)objects[objects.length - 1];

                String[] action = aiClient.playTurn();
                ack.call((Object)action);
            }
        };
    }

    /**
     * Ecouteur du message Synchronize, qui signifie au joueur que les informations de la partie ont été mises à jour.
     */
    public InjectableListener synchronizeMessageListener(AIClient aiClient, AIPlayer aiPlayer)
    {
        return new InjectableListener(aiClient, aiPlayer)
        {
            @Override
            public void call(Object... objects)
            {
                ArrayList<Invention> inventions = gsonSerializer.fromJson((String)objects[1],
                        new TypeToken<ArrayList<Invention>>(){}.getType());
                HashMap<UUID, List<Inventor>> playerInventors = gsonSerializer.fromJson((String)objects[0],
                        new TypeToken<HashMap<UUID, List<Inventor>>>(){}.getType());
                
                aiClient.sync(inventions, playerInventors, aiClient, aiPlayer);
            }
        };
    }

    /**
     * Ecouteur du message GameEnded qui permet de connaitre le gagnant ou le eprdant d'une partie
     */
    public Emitter.Listener gameEndedMessageListener = objects ->
    {
        boolean victory = (boolean)objects[0];

        if (victory)
        {
            log.info(String.format("Vous avez gagné avec %d point(s) !", objects[1]));
        }
        else
        {
            log.info(String.format("Vous avez perdu avec %d point(s) ...", objects[1]));
        }

        this.availableInventions.clear();
        this.aiPlayer.refreshInventors(new ArrayList<>());
        this.otherPlayers.clear();

        this.onGameEnded(victory);
    };

    /**
     * Ecouteur du message LogMessage qui permet de forcer l'affichage d'un message depuis le serveur
     */
    public Emitter.Listener logMessageListener = objects ->
    {
        if(displayLog)
            log.info(objects[0].toString());
    };

    /**
     * Ecouteur du message LogMessage qui permet de forcer l'affichage d'un message depuis le serveur
     */
    public Emitter.Listener disconnectListener = objects ->
    {
        super.init();
        this.registerHandlers();

        this.onGameClosed();
    };

    /**
     * Permet d'initialiser l'intelligence artificielle.
     */
    public void init(AIClient aiClient, AIPlayer aiPlayer) throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException
    {
        Constructor<?> ctor = this.strategyType.getConstructor(Player.class, UUIDProvider.class, InventionProvider.class);
        Strategy strategy = (Strategy)ctor.newInstance(aiPlayer, aiClient, aiClient);

        aiPlayer.setStrategy(strategy);

        if(displayLog)
            log.info(String.format("Initialisation de l'intelligence artificielle '%s' terminée.", this.strategyType.getName()));
    }

    /**
     * Méthode permettant de "jouer" le tour courant.
     * @return l'action a effectuer
     */
    public String[] playTurn()
    {
        return this.aiPlayer.requestAction();
    }

    /**
     * Méthode permettant de "synchroniser" le client
     */
    public void sync(List<Invention> inventions, Map<UUID, List<Inventor>> playersInventors, AIClient aiClient, AIPlayer aiPlayer)
    {
        //mise à jour des inventions
        aiClient.availableInventions = (ArrayList<Invention>) inventions;

        //mise à jour des inventeurs
        aiPlayer.refreshInventors(playersInventors.get(UUID.fromString(aiClient.getUUID())));
        playersInventors.remove(UUID.fromString(aiClient.getUUID()));

        aiClient.otherPlayers = (HashMap<UUID, List<Inventor>>) playersInventors;

        if(displayLog)
            log.info("Mise à jour des informations de la partie effectuée.");
    }

    /**
     * Retourne une liste des inventions disponibles sur le plateau.
     * @return liste des inventions disponibles sur le plateau.
     */
    public ArrayList<Invention> getInventions()
    {
        return this.availableInventions;
    }

    /**
     * Retourne un dictionnaire des inventeurs des autres joueurs de la partie indexé par UUID
     */
    public Map<UUID, List<Inventor>> getOthersPlayersInventors()
    {
        return this.otherPlayers;
    }

    //region Listeners

    public void addListener(AIClientListener listener)
    {
        this.listeners.add(listener);
    }

    private void onGameEnded(boolean victory)
    {
        for(AIClientListener listener : this.listeners)
        {
            listener.onGameEnded(victory);
        }
    }

    private void onGameClosed()
    {
        for(AIClientListener listener : this.listeners)
        {
            listener.onGameClosed();
        }
    }

    //endregion
}





