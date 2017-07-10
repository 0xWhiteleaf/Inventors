package engine.core;

import engine.core.rewards.Reward;
import engine.core.rewards.RewardType;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Classe représentant une invention.
 * @author Alexandre Ciarafoni & Louis Prud'homme
 */
public class Invention
{
    public static final int NUMBER_OF_REWARDS_BY_INVENTION = 3;

    private int era;
    private String name;

    private Knowledges requiredKnowledges;
    private Knowledges actualKnowledges;

    private HashMap<UUID, Integer> contributions;
    private ArrayList<Reward> rewards;

    /**
     * Constructor
     * @param name Nom de l'invention
     */
    public Invention(String name, Knowledges knowledges, int era)
    {
        this.era = era;
        this.name = name;

        this.requiredKnowledges = knowledges;
        this.actualKnowledges = new Knowledges();

        this.rewards = new ArrayList<>();
        this.contributions = new HashMap<>();

        this.rewards.add(new Reward(RewardType.CARD, this.getVictoryPoints()));
    }

    /**
     * Accessseur de name
     * @return le nom de l'invention
     */
    public String getName()
    {
        return name;
    }

    /**
     * Accesseur de requiredKnowledges
     * @return les connaissances nécessaire à la complétion de l'invention
     */
    public Knowledges getRequiredKnowledges()
    {
        return requiredKnowledges;
    }

    /**
     * Accesseur de actualKnowledges
     * @return les connaissances actuelles de l'invention
     */
    public Knowledges getActualKnowledges() { return actualKnowledges; }

    /**
     * Permet de mettre à jour les connaissances déjà apportées à une invention avec celles d'un inventeur.
     * @param inventorKnowledges connaissances de l'inventeur travaillant sur l'invention
     */
    public void updateActualKnowledges(Player player, Knowledges inventorKnowledges)
    {
        if(inventorKnowledges.getChem() > 0 && requiredKnowledges.getChem() > 0) {
            for (int i = 0; i < inventorKnowledges.getChem() && actualKnowledges.getChem() < requiredKnowledges.getChem(); i++) {
                actualKnowledges.modChem(1);
                this.contributions.put(player.getUUID(),
                        this.contributions.get(player.getUUID()) == null ? 1 : (this.contributions.get(player.getUUID()) + 1));
            }
        }

        if(inventorKnowledges.getMech() > 0 && requiredKnowledges.getMech() > 0) {
            for (int i = 0; i < inventorKnowledges.getMech() && actualKnowledges.getMech() < requiredKnowledges.getMech(); i++) {
                actualKnowledges.modMech(1);
                this.contributions.put(player.getUUID(),
                        this.contributions.get(player.getUUID()) == null ? 1 : (this.contributions.get(player.getUUID()) + 1));
            }
        }

        if(inventorKnowledges.getMath() > 0 && requiredKnowledges.getMath() > 0) {
            for(int i = 0; i < inventorKnowledges.getMath() && actualKnowledges.getMath() < requiredKnowledges.getMath(); i++) {
                actualKnowledges.modMath(1);
                this.contributions.put(player.getUUID(),
                        this.contributions.get(player.getUUID()) == null ? 1 : (this.contributions.get(player.getUUID()) + 1));
            }
        }

        if(inventorKnowledges.getPhys() > 0 && requiredKnowledges.getPhys() > 0) {
            for(int i = 0; i < inventorKnowledges.getPhys() && actualKnowledges.getPhys() < requiredKnowledges.getPhys(); i++) {
                actualKnowledges.modPhys(1);
                this.contributions.put(player.getUUID(),
                        this.contributions.get(player.getUUID()) == null ? 1 : (this.contributions.get(player.getUUID()) + 1));
            }
        }
    }

    /**
     * Permet de savoir si l'invention e a été terminée.
     * @return un boolean valant true si l'invention est complétée.
     */
    public boolean isCompleted()
    {
        boolean completed = false;
        if((requiredKnowledges.getMech() <= actualKnowledges.getMech())  &&
            (requiredKnowledges.getPhys() <= actualKnowledges.getPhys()) &&
            (requiredKnowledges.getChem() <= actualKnowledges.getChem()) &&
            (requiredKnowledges.getMath() <= actualKnowledges.getMath()) )
                    {
                        completed = true;
                    }
        return completed;
    }

    /**
     * Permet de récupérer la valeur de victoryPoints.
     * @return un int donnant le nombre de point gagné lors de l'achèvement de l'invention.
     */
    public int getVictoryPoints()
    {
        return this.era + this.rewards.stream().collect(Collectors.summingInt(i -> i.getValue()));
    }

    /**
     * Permet de récupérer la valeur de era
     * @return un int donnant l'époque de l'invention
     */
    public int getEra()
    {
        return this.era;
    }

    /**
     * Renvoie la liste des pions de récompenses affectés à l'ivnention.
     * @return liste des pions de récompenses affectés à l'ivnention.
     */
    public List<Reward> getRewards()
    {
        return this.rewards;
    }

    /**
     * Permet de tirer les pions de récompense associés à l'invention.
     */
    public void pickRewardPawns()
    {
        for(int i = 0; i < NUMBER_OF_REWARDS_BY_INVENTION - 1; i++)
            this.rewards.add(new Reward(RewardType.VICTORY));
    }

    /**
     * @return hashmap des contributeurs
     */
    public HashMap<UUID, Integer> getContributions()
    {
        return this.contributions;
    }
}
