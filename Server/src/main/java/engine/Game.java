package engine;

import com.corundumstudio.socketio.SocketIOClient;
import com.github.javafaker.Faker;
import com.google.gson.Gson;
import engine.actions.*;
import engine.comparators.ContributorComparator;
import engine.core.Invention;
import engine.core.Inventor;
import engine.core.Player;
import engine.core.Team;
import engine.core.rewards.Reward;
import engine.core.rewards.RewardType;
import engine.exceptions.*;
import engine.managers.InventionManager;
import engine.managers.InventorManager;
import engine.managers.exceptions.DuplicateInventionException;
import engine.managers.exceptions.DuplicateInventorException;
import engine.reflection.Singleton;
import network.ProtocolMessages;
import network.callbacks.ActionAckCallback;
import network.callbacks.RewardAckCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Classe modélisant une partie.
 * La méthode init doit être appelée avant le début de chaque nouvelle partie.
 * @author Valentin Sappa, Noé Mourton-Comte, Alexandre Ciarafoni, Barry Hezam
 */
public class Game
{
    private static final Logger log = LoggerFactory.getLogger(Game.class);

    private static final int CARDS_PER_PLAYER = 4;
    public static final int REQUIRED_PLAYERS = 2;

    private boolean isStarted;
    private HashMap<SocketIOClient, Player> players;
    private ArrayList<Team> takenTeams;

    private ArrayList<Invention> inventionsList;
    private int currentEra;

    private Gson gsonSerializer = new Gson();
    private Faker faker = new Faker(new Locale("fr"));
    private Object locker;

    //region Initialisation de la partie

    public Game(List<SocketIOClient> clients)
    {
        this.locker = new Object();

        this.players = new HashMap<>();
        this.takenTeams = new ArrayList<>();

        for(SocketIOClient client : clients)
        {
            this.players.put(client, new Player(faker.name().firstName(), client.getSessionId()));
        }

        this.inventionsList = new ArrayList<>();
    }

    public void init() throws DuplicateInventorException, DuplicateInventionException
    {
        log.info("Initialisation de la partie en cours...");

        this.isStarted = true;

        //Initialisation des Inventions et des Inventeurs
        Singleton.getInstance(InventorManager.class).load();
        Singleton.getInstance(InventionManager.class).load();

        //Tirage de quatre cartes par joueurs...
        for (Map.Entry<SocketIOClient, Player> playerEntry : this.getPlayers().entrySet())
        {
            Player player = playerEntry.getValue();

            //Récupération d'une équipe pour le joueur
            Team playerTeam = this.getTeamForPlayer();
            this.takenTeams.add(playerTeam);

            this.pickInventorsForPlayer(player, playerTeam);
        }

        //Le trophé est donné au premier joueur
        if(this.getPlayers().values().size() > 0)
        {
            ((Player) this.getPlayers().values().toArray()[0]).giveTrophy();
        }

        //Ajout d'inventions sur la "table" pour l'époque 1
        this.currentEra = 1;
        this.pickInventionsForEra();

        log.info("Initialisation de la partie terminée !");

        //Envoi d'un "GameStartedMessage" pour initializer le début de la partie chez les clients...
        this.broadcastEvent(ProtocolMessages.GameStartedMessage.name(),
                this.gsonSerializer.toJson(this.getMergedInventors()),
                this.gsonSerializer.toJson(this.getInventionsList()));
    }

    /**
     * Fonction permettant de tirer autant de cartes que le nombre de joueurs plus trois pour l'époque courante
     */
    private void pickInventionsForEra()
    {
        this.inventionsList.clear();

        for(int i = 0; i < this.REQUIRED_PLAYERS + 3; i++)
        {
            Invention invention = Singleton.getInstance(InventionManager.class).pickInvention(this.getCurrentEra());
            invention.pickRewardPawns();
            this.inventionsList.add(invention);
        }
    }

    /**
     * Fonction permettant de tirer quatre inventeurs pour un joueur donné en paramètre
     * @param player joueur n'ayant pas encore pioché de carte
     */
    private void pickInventorsForPlayer(Player player, Team team)
    {
        List<Inventor> inventors = Singleton.getInstance(InventorManager.class).pickInventor(team);
        player.addAllInventors(inventors);
    }

    //endregion

    //region Déroulement de la partie

    /**
     * Méthode permettant de jouer le prochain tour
     * @throws CurrentPlayerNotFoundException
     */
    public void play() throws CurrentPlayerNotFoundException, CantDeterminateWinnerException
    {
        log.info("Exécution du prochain tour...");

        Optional<Map.Entry<SocketIOClient, Player>> currentPlayerResult = this.getPlayers().entrySet().stream()
                .filter(entry -> entry.getValue().hasLeonardoDaVinciTrophy()).findFirst();

        if(currentPlayerResult.isPresent())
        {
            Map.Entry<SocketIOClient, Player> currentPlayerEntry = currentPlayerResult.get();

            //Récupération du client associé au joueur
            SocketIOClient currentClient = currentPlayerEntry.getKey();
            //Récupération du joueur
            Player currentPlayer = currentPlayerEntry.getValue();

            //Nous demandons au joueur l'action qu'il souhaite effectuer, puis nous l'exécutons...
            this.requestAndExecuteAction(currentClient, currentPlayer);

            //Nous vérifions si la partie est terminée ou non
            if(this.isFinished())
            {
                this.terminate();
            }
            else
            {
                //Suppression du trophée au joueur qui vient de jouer ce tour
                currentPlayerEntry.getValue().removeTrophy();

                //Le trophée est maintenant donné au joueur qui jouera le prochain tour
                Map.Entry<SocketIOClient, Player> nextPlayerEntry = null;

                Iterator<Map.Entry<SocketIOClient, Player>> i = this.getPlayers().entrySet().iterator();
                while (i.hasNext() && nextPlayerEntry == null)
                {
                    Map.Entry<SocketIOClient, Player> entry = i.next();
                    if(entry == currentPlayerEntry && i.hasNext())
                        nextPlayerEntry = i.next();
                }

                //Si il n'y a plus d'entrée supérieure, nous revenons à la première
                if(nextPlayerEntry == null)
                    nextPlayerEntry = this.getPlayers().entrySet().iterator().next();

                nextPlayerEntry.getValue().giveTrophy();

                //Si la partie n'est pas terminée, le tour suivant est joué.
                this.play();
            }
        }
        else
        {
            throw new CurrentPlayerNotFoundException("Impossible de déterminer le joueur qui doit jouer ce tour !");
        }
    }

    public void requestAndExecuteAction(SocketIOClient currentClient, Player currentPlayer)
    {
        try
        {
            //Récupération puis exécution de l'action du joueur...
            ActionAckCallback actionAckCallback = new ActionAckCallback(this.locker, this, currentPlayer);
            Action playerAction = this.requestAction(currentClient, actionAckCallback, true);

            if(playerAction != null)
            {
                playerAction.execute();

                String playerActionLogMessage = String.format("Le joueur %s a effectué l'action : %s",
                        currentPlayer.getName(), playerAction.toString());
                log.info(playerActionLogMessage);

                //Envoi d'un message pour afficher l'action chez tous les clients sauf celui a effectué l'action
                ArrayList<UUID> toExclude = new ArrayList<>();
                toExclude.add(currentClient.getSessionId());

                this.broadcastEvent(toExclude,
                        ProtocolMessages.LogMessage.name(), playerActionLogMessage);

                //Envoi d'un message pour afficher l'action chez le joueur qui a effecuté l'action
                currentClient.sendEvent(ProtocolMessages.LogMessage.name(),
                        String.format("Vous avez effectué l'action : %s", playerAction.toString()));

                if (playerAction instanceof WorkAction)
                {
                    WorkAction workAction = (WorkAction)playerAction;
                    Invention targetedInvention = workAction.getTargetedInvention();

                    //Nous vérifions si la dernière action a terminée une invention
                    if(targetedInvention.isCompleted())
                    {
                        this.requestAndAddRewards(currentPlayer, workAction);
                    }

                    //Nous vérifions si l'époque est terminée...
                    if (this.eraCompleted() && this.currentEra < 3)
                    {
                        //Ajout d'inventions sur la "table" pour l'époque suivante
                        this.currentEra++;
                        this.pickInventionsForEra();
                        log.info(String.format("L'époque %d a débuté !", this.currentEra));
                    }
                }

                //Envoi d'un message pour la synchronisation des joueurs...
                this.broadcastEvent(ProtocolMessages.SynchronizeMessage.name(),
                        this.gsonSerializer.toJson(this.getMergedInventors()),
                        this.gsonSerializer.toJson(this.getInventionsList()));
            }
            else
            {
                throw new PlayerNotRespondingException("aucune action n'a été communiquée.");
            }
        }
        catch (PlayerNotRespondingException ex)
        {
            log.error(String.format("Le joueur %s ne répond plus : %s", currentPlayer.getName(), ex.getMessage()));
            this.kick(currentClient);
        }
        catch (Exception ex)
        {
            log.error(String.format("Le joueur %s a essayé d'effectuer une action impossible !",
                    currentPlayer.getName()));
            log.error(String.format("Détails concernant l'action : %s", ex.getMessage()));
            this.kick(currentClient);
        }
    }

    /**
     * Fonction permettant d'intérroger le joueur sur l'action qu'il souhaite effectuer
     */
    public Action requestAction(SocketIOClient playerClient, ActionAckCallback actionAckCallback, boolean wait) throws InterruptedException
    {
        synchronized (this.locker)
        {
            playerClient.sendEvent(ProtocolMessages.TurnStartedMessage.name(), actionAckCallback);
            if(wait)
                this.locker.wait();

            return actionAckCallback.getAction();
        }
    }

    public void requestAndAddRewards(Player currentPlayer, WorkAction workAction) throws InterruptedException, PlayerNotRespondingException
    {
        Invention targetedInvention = workAction.getTargetedInvention();

        //Nous demandons à chaque joueur ayant participé à la complétion
        // de l'invention la récompense qu'il souhaite choisir...
        Map<SocketIOClient, Player> contributors = this.getPlayers().entrySet().stream()
                .filter(entry -> targetedInvention.getContributions().get(entry.getValue().getUUID()) != null)
                .sorted(new ContributorComparator(currentPlayer, targetedInvention))
                .collect(Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a, LinkedHashMap::new));

        List<Reward> availableRewards = workAction.getTargetedInvention().getRewards();

        for(Map.Entry<SocketIOClient, Player> contributor : contributors.entrySet())
        {
            try
            {
                log.info(String.format("%s doit choisir une récompense pour sa contribution de %d cube(s) à l'invention '%s'...",
                        contributor.getValue().getName(),
                        targetedInvention.getContributions().get(contributor.getValue().getUUID()),
                        targetedInvention.getName()));

                RewardAckCallback rewardAckCallback = new RewardAckCallback(this.locker,
                        availableRewards);

                Reward selectedReward = this.requestReward(contributor.getKey(), rewardAckCallback,
                        workAction.getTargetedInvention().getRewards(), true);

                if (selectedReward != null)
                {
                    contributor.getValue().addReward(selectedReward);

                    //Si le joueur à choisit la carte, nous l'ajoutons à sa liste d'inventions complétées...
                    if (selectedReward.getType() == RewardType.CARD)
                        contributor.getValue().addCompletedInvention(targetedInvention);

                    availableRewards.remove(selectedReward);

                    log.info(String.format("%s a choisit la récompense suivante : %s.", contributor.getValue().getName(),
                            selectedReward.toString()));
                }
                else
                {
                    throw new PlayerNotRespondingException("aucun choix de récompense n'a été communiqué.");
                }
            }
            catch (PlayerNotRespondingException ex)
            {
                log.error(String.format("Le joueur %s ne répond plus : %s", contributor.getValue().getName(), ex.getMessage()));
                this.kick(contributor.getKey());
            }
        }
    }


    /**
     * Fonction permettant d'intérroger le joueur sur la récompense qu'il souhaite acquérir
     */
    public Reward requestReward(SocketIOClient playerClient, RewardAckCallback rewardAckCallback, List<Reward> availableRewards, boolean wait) throws InterruptedException
    {
        synchronized (this.locker)
        {
            playerClient.sendEvent(ProtocolMessages.RewardMessage.name(), rewardAckCallback, availableRewards);
            if(wait)
                this.locker.wait();

            return rewardAckCallback.getSelectedReward();
        }
    }

    /**
     * Fonction permettant de déterminer le gagnant
     */
    public Map.Entry<SocketIOClient, Player> determinateWinner() throws CantDeterminateWinnerException
    {
        Map.Entry<SocketIOClient, Player> winnerEntry = null;

        if(this.getPlayers().size() > 1)
        {
            int maxVictoryPoints = 0;

            for(Map.Entry<SocketIOClient, Player> playerEntry : this.getPlayers().entrySet())
            {
                int playerVictoryPoints = this.getPlayerVictoryPoint(playerEntry.getValue());

                if(playerVictoryPoints > maxVictoryPoints)
                {
                    maxVictoryPoints = playerVictoryPoints;
                    winnerEntry = playerEntry;
                }
            }
        }
        else
        {
            //Si il n'y a pas de gagnant légitime, le dernier joueur encore dans la partie a gagné.
            Optional<Map.Entry<SocketIOClient, Player>> winnerResult = this.getPlayers().entrySet().stream().findAny();
            if(winnerResult.isPresent())
            {
                winnerEntry = winnerResult.get();
            }
        }

        if(winnerEntry == null)
            throw new CantDeterminateWinnerException("Impossible de déterminer le gagnant !");

        return winnerEntry;
    }

    /**
     * Méthode permettant de mettre fin à la partie
     */
    public void terminate()
    {
        try
        {
            Map.Entry<SocketIOClient, Player> winnerEntry = this.determinateWinner();

            SocketIOClient winnerClient = winnerEntry.getKey();

            //Envoi d'un message au gagnant
            winnerClient.sendEvent(ProtocolMessages.GameEndedMessage.name(), true, this.getPlayerVictoryPoint(winnerEntry.getValue()));

            //Envoi d'un message aux perdants
            for(Map.Entry<SocketIOClient, Player> playerEntry : this.getPlayers().entrySet())
            {
                if(playerEntry.getKey() != winnerEntry.getValue())
                {
                    playerEntry.getKey().sendEvent(ProtocolMessages.GameEndedMessage.name(), false, this.getPlayerVictoryPoint(playerEntry.getValue()));
                }
            }

            log.info(String.format("Le joueur %s remporte la partie avec %d point(s) !", winnerEntry.getValue().getName(),
                    this.getPlayerVictoryPoint(winnerEntry.getValue())));

        }
        catch (CantDeterminateWinnerException ex)
        {
            log.error(ex.getMessage());
        }

        log.info("Partie terminée.");
    }

    /**
     * Méthode permettant d'expulser un joueur de la partie
     */
    public void kick(SocketIOClient client)
    {
        client.sendEvent(ProtocolMessages.KickedMessage.name(), "");
        client.disconnect();

        this.players.remove(client);
    }

    //endregion

    //region Gestion de la partie

    /**
     * Fonction permettant de récupérer une équipe pour un joueur
     */
    public Team getTeamForPlayer()
    {
        Team playerTeam = Team.Rouge;

        int i = 0;
        while(this.takenTeams.contains(playerTeam) && i < Team.values().length)
        {
            playerTeam = Team.values()[i];
            i++;
        }

        return playerTeam;
    }

    /**
     * Fonction permettant de récupérer le nombre de points d'un joueur
     * @param player UUID du joueur
     */
    public int getPlayerVictoryPoint(Player player)
    {
        int playerVictoryPoints = 0;

        for(Reward reward : player.getRewards())
        {
            playerVictoryPoints += reward.getValue();
        }

        return playerVictoryPoints;
    }

    /**
     * Fonction permettant de connaître l'époque actuelle
     * @return
     */
    public int getCurrentEra()
    {
        return this.currentEra;
    }

    /**
     * Fonction permettant de récupérer la liste des joueurs
     */
    public HashMap<SocketIOClient, Player> getPlayers()
    {
        return this.players;
    }

    /**
     * Fonction permettant de récupérer la liste des inventions présente "sur la table"
     * @return
     */
    public ArrayList<Invention> getInventionsList()
    {
        return this.inventionsList;
    }

    /**
     * Fonction permettant de récupérer un dictionnaire de tous les inventeurs de la partie indexé par UUID
     */
    public HashMap<UUID, List<Inventor>> getMergedInventors()
    {
        HashMap<UUID, List<Inventor>> mergedInventors = new HashMap<>();

        for(Map.Entry<SocketIOClient, Player> playerEntry : this.getPlayers().entrySet())
        {
            mergedInventors.put(playerEntry.getKey().getSessionId(), playerEntry.getValue().getInventors());
        }

        return mergedInventors;
    }

    /**
     * Fonction permetant de récupérer l'inventeur d'un joueur par son nom
     * @param player joueur
     * @param inventorName nom de l'inventeur
     */
    public Inventor getPlayerInventorByName(Player player, String inventorName) throws RequestedInventorNotFoundException
    {
        Optional<Inventor> inventor = player.getInventors().stream()
                .filter(entry -> entry.getName().equals(inventorName)).findFirst();

        if(inventor.isPresent())
        {
            return inventor.get();
        }
        else
        {
            throw new RequestedInventorNotFoundException("Le joueur ne possède pas cet inventeur !");
        }
    }

    /**
     * Fonction permettant de récupérer une invention par son nom
     * @param inventionName nom de l'invention
     */
    public Invention getInventionByName(String inventionName) throws RequestedInventionNotFoundException
    {
        Optional<Invention> invention = this.getInventionsList().stream()
                .filter(entry -> entry.getName().equals(inventionName)).findFirst();
        if(invention.isPresent())
        {
            return invention.get();
        }
        else
        {
            throw new RequestedInventionNotFoundException("Cette invention n'existe pas !");
        }
    }

    /**
     * Fonction permettant de savoir si une époque est terminée
     */
    public boolean eraCompleted()
    {
        boolean completed = false;

        ArrayList<Invention> completedInventions = this.getInventionsList().stream()
                .filter(Invention::isCompleted)
                .collect(Collectors.toCollection(ArrayList::new));

        if(completedInventions.size() == this.getInventionsList().size() - 1)
        {
            completed = true;
        }

        return completed;
    }

    /**
     * Fonction permettant de savoir si une partie est terminée
     */
    public boolean isFinished()
    {
        boolean finished = false;

        if(this.getPlayers().size() <= 1 || this.getCurrentEra() == 3 && this.eraCompleted())
        {
            finished = true;
        }

        return finished;
    }

    //endregion

    //region Gestion des clients

    /**
     * Méthode permettant d'envoyer un évenement à tous les clients connectés
     * @param eventName nom de l'événement
     * @param params paramètres
     */
    public void broadcastEvent(String eventName, Object... params)
    {
        for (SocketIOClient client : this.getPlayers().keySet())
        {
            client.sendEvent(eventName, params);
        }
    }

    /**
     * Méthode permettant d'envoyer un évenement à tous les clients connectés avec la possibilité d'exclure certains client
     * @param eventName nom de l'événement
     * @param params paramètres
     */
    public void broadcastEvent(ArrayList<UUID> toExclude, String eventName, Object... params)
    {
        List<SocketIOClient> clients = this.getPlayers().keySet().stream()
                .filter(entry -> !toExclude.contains(entry.getSessionId()))
                .collect(Collectors.toList());

        for(SocketIOClient client : clients)
        {
            client.sendEvent(eventName, params);
        }
    }

    //endregion
}