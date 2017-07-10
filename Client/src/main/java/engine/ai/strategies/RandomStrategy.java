package engine.ai.strategies;

import engine.ai.strategies.exceptions.NoOneCanWorkException;
import engine.ai.strategies.providers.*;

import engine.core.Invention;
import engine.core.Inventor;
import engine.core.Player;
import engine.core.rewards.Reward;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Classe modélisant une stratégie d'IA complètement aléatoire.
 * @author Louis Prud'homme
 */
public class RandomStrategy extends Strategy
{
    private Random randomizer;

    /**
     * Constructeur.
     * @param inventionProvider interface fournissant l'accès aux données des inventions en jeu.
     * @param player joueur pour lequel la stratégie va calculer des actions.
     */
    public RandomStrategy(Player player, UUIDProvider uuidProvider, InventionProvider inventionProvider)
    {
        super(player, uuidProvider, inventionProvider);

        this.randomizer = new Random();
    }

    /**
     * Permet d'obtenir une action déterminée au hasard pour le joueur cible.
     * @return action déterminée au hasard.
     */
    @Override
    public String[] determinateAction()
    {
        String[] actionToDo;
        if(this.player.getBusyInventors().size() > 0)
        {
            if(this.player.getBusyInventors().size() == this.player.getInventors().size())
            {
                actionToDo = createMakeAvalaibleAction();
            }
            else
            {
                if(randomizer.nextInt(2) == 0)
                {
                    actionToDo = createMakeAvalaibleAction();
                }
                else
                {
                    try
                    {
                        Invention randomInvention = pickRandomInvention();
                        actionToDo = createWorkAction(pickRandomInventor(randomInvention), randomInvention);
                    } catch (NoOneCanWorkException e)
                    {
                        actionToDo = createMakeAvalaibleAction();
                    }
                }
            }
        }
        else
        {
            try
            {
                Invention randomInvention = pickRandomInvention();
                actionToDo = createWorkAction(pickRandomInventor(randomInvention), randomInvention);
            } catch (NoOneCanWorkException e)
            {
                actionToDo = createMakeAvalaibleAction();
            }
        }
        return actionToDo;
    }

    /**
     * Détermine une recompense parmi celles proposées.
     * @param avalaibleRewards récompenses disponibles
     * @return récompense choisie.
     */
    @Override
    public Integer determinateReward(ArrayList<Reward> avalaibleRewards)
    {
        return randomizer.nextInt(avalaibleRewards.size());
    }

    /**
     * Permet de choisir un inventeur au hasard dans la liste des inventeurs disponibles.
     * Pour tirer un inventeur, il faut s'assurer qu'il puisse travailler sur l'invention tirée.
     * @param randomInvention invention aléatoire pour laquelle on va tirer un inventeur.
     * @return inventeur choisi par le hasard.
     */
    private Inventor pickRandomInventor(Invention randomInvention)
    {
        ArrayList<Inventor> inventorArrayList = this.player.getFreeInventors();
        Inventor randomInventor = inventorArrayList.get(randomizer.nextInt(inventorArrayList.size()));
        while(!randomInventor.canWorkOn(randomInvention))
            randomInventor = inventorArrayList.get(randomizer.nextInt(inventorArrayList.size()));
        return randomInventor;
    }

    /**
     * Permet de choisir une invention au hasard dans la liste des inventions disponibles.
     * Pour tirer une invention, il faut s'assurer qu'au moins un inventeur puisse travailler dessus.
     * @throws NoOneCanWorkException erreur dans le cas ou aucun inventeur ne peut travailler sur aucune invention.
     * @return invention choisie par le hasard.
     */
    private Invention pickRandomInvention() throws NoOneCanWorkException
    {
        List<Invention> inventions = this.inventionProvider.getInventions();
        Invention randomInvention;
        boolean someOneCanWork = false;
        int randomIndex = randomizer.nextInt(inventions.size()), i = randomIndex;
        int limite = inventions.size() + randomIndex + 1;

        while(!someOneCanWork && i < limite)
        {
            for(Inventor iv : player.getFreeInventors())
            {
                if (iv.canWorkOn(inventions.get(i % inventions.size())))
                    someOneCanWork = true;
            }

            if(!someOneCanWork)
                i++;
        }

        if(someOneCanWork)
            randomInvention = inventions.get(i % inventions.size());
        else
            throw new NoOneCanWorkException("Aucun inventeur du joueur ne peut travailler sur aucune invention.");

        return randomInvention;
    }

    /**
     * Permet de définir l'instance de Random qui sera utiliée lors des calculs.
     * @param r nouvelle instance de Random à utiliser lors des prochains calculs.
     */
    public void setRandomizer(Random r)
    {
        randomizer = r;
    }
}