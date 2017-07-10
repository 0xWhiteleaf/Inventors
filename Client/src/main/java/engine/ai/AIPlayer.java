package engine.ai;

import engine.ai.strategies.Strategy;
import engine.core.Inventor;
import engine.core.Player;
import engine.core.rewards.Reward;

import java.util.ArrayList;
import java.util.List;

/**
 * Modélise un joueur controlé par une intelligence artificielle
 * @author Barry Hezam
 */
public class AIPlayer extends Player
{
    private Strategy strategy;

    public AIPlayer ()
    {
        super();
    }

    /**
     * Méthode permettant d'assigner une stratégie à l'intelligence artificielle
     * @param strategy stratégie à appliquer
     */
    public void setStrategy(Strategy strategy)
    {
        this.strategy = strategy;
    }

    /**
     * Demande à l'IA une action pour le tour courant
     * @return l'action choisie par la stratégie de l'IA
     */
    public String[] requestAction()
    {
        return this.strategy.determinateAction();
    }

    /**
     * Demande à l'IA de choisir une récompense parmi celles disponibles.
     * @param availaibleRewards récompenses disponibles.
     * @return récompense choisie.
     */
    public Integer requestReward(ArrayList<Reward> availaibleRewards)
    {
        return this.strategy.determinateReward(availaibleRewards);
    }

    /**
     * Permet de rafraîchir la liste des inventeurs du joueur.
     * @param refreshedInventors liste des inventeurs actualisée.
     */
    public void refreshInventors(List<Inventor> refreshedInventors)
    {
        synchronized (super.inventors) {
            super.inventors.clear();
            super.inventors.addAll(refreshedInventors);
        }
    }
}
