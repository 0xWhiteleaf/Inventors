package engine.core;

import engine.core.rewards.Reward;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Classe représentant un joueur.
 * @author Noé Mourton-Comte
 */
public class Player
{
    private String name;
    private UUID uuid;

    protected List<Inventor> inventors;
    private List<Invention> completedInventions;
    private List<Reward> rewards;

    private boolean hasLeonardoDaVinciTrophy;

    public Player(String name, UUID uuid)
    {
        this();
        this.name = name;
        this.uuid = uuid;
    }

    public Player()
    {
        this.inventors = Collections.synchronizedList(new ArrayList<>());
        this.completedInventions = Collections.synchronizedList(new ArrayList<>());
        this.rewards = Collections.synchronizedList(new ArrayList<>());

        this.hasLeonardoDaVinciTrophy = false;
    }

    /**
     * Méthode permettant d'ajouter un inventeur à la liste d'inventeurs du joueur
     * @param inventor Inventeur
     */
    public void addInventor(Inventor inventor)
    {
        synchronized (this.inventors) {
            if (!this.inventors.contains(inventor))
                this.inventors.add(inventor);
        }
    }

    /**
     * Méthode permettant d'ajouter une équipe d'inventeurs à la liste d'inventeurs du joueur
     * @param inventors Liste d'inventeurs
     */
    public void addAllInventors(List<Inventor> inventors)
    {
        synchronized (this.inventors) {
            this.inventors.addAll(inventors);
        }
    }

    /**
     * Méthode permettant d'ajouter une invention à la liste d'inventions complétée du joueur
     * @param invention
     */
    public void addCompletedInvention(Invention invention)
    {
        synchronized (this.completedInventions) {
            if(!this.completedInventions.contains(invention))
            {
                this.completedInventions.add(invention);
            }
        }
    }

    /**
     * Méthode permettant d'ajouter une récompense à la liste de récompenses du joueur
     * @param reward
     */
    public void addReward(Reward reward)
    {
        synchronized (this.rewards) {
            this.rewards.add(reward);
        }
    }

    /**
     * Méthode permettant de donner au joueur la statuette Léonard de Vinci
     */
    public void giveTrophy()
    {
        this.hasLeonardoDaVinciTrophy = true;
    }

    /**
     * Méthode permettant de retirer au joueur la statuette Léonard de Vinci
     */
    public void removeTrophy()
    {
        this.hasLeonardoDaVinciTrophy = false;
    }

    /**
     * Fonction permettant de récupérer la liste d'inventeurs du joueur
     * @return ArrayList d'Inventor
     */
    public List<Inventor> getInventors()
    {
        return this.inventors;
    }

    /**
     * Fonction permettant de récupérer la liste d'inventions complétées du joueur
     * @return ArrayList d'Invention
     */
    public List<Invention> getCompletedInventions()
    {
        return this.completedInventions;
    }

    /**
     * Fonction permettant de récupérer la liste de récompenses du joueur
     * @return ArrayList de Recompense
     */
    public List<Reward> getRewards()
    {
        return this.rewards;
    }

    /**
     * Fonction permettant de savoir si le joueur détient la statuette Léonard de Vinci
     * @return boolean
     */
    public boolean hasLeonardoDaVinciTrophy()
    {
        return this.hasLeonardoDaVinciTrophy;
    }

    /**
     * Méthode permettant de se renseigner sur les inventeurs inoccupés du joueur ;
     * @return liste des inventeurs inoccupés du joueur.
     */
    public ArrayList<Inventor> getFreeInventors()
    {
        return this.inventors.stream()
                .filter(i -> !i.isBusy())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Méthode permettant de se renseigner sur les inventeurs occupés du joueur cible;
     * @return liste des inventeurs occupés du joueur cible.
     */
    public ArrayList<Inventor> getBusyInventors()
    {
        //System.out.println(this.inventors);
        return this.inventors.stream()
                .filter(i -> i.isBusy())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Fonction permettant de récupérer le nom du joueur
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Fonction permettant de récupérer l'UUID du joueur
     */
    public UUID getUUID()
    {
        return this.uuid;
    }
}
