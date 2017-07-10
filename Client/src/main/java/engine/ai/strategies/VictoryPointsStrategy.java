package engine.ai.strategies;
import engine.ai.strategies.providers.InventionProvider;
import engine.ai.strategies.providers.UUIDProvider;
import engine.core.*;
import engine.core.rewards.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Classe modélisant une stratégie d'IA visant les points de victoire.
 * @author Louis Prud'homme
 */
public class VictoryPointsStrategy extends Strategy
{
    /**
     * Constructeur de la stratégie.
     * @param inventionProvider interface fournissant l'accès aux données des inventions en jeu.
     * @param player joueur pour lequel la stratégie va définir les actions.
     */
    public VictoryPointsStrategy(Player player, UUIDProvider uuidProvider, InventionProvider inventionProvider)
    {
        super(player, uuidProvider, inventionProvider);
    }

    /**
     * Permet de déterminer une action pour le joueur.
     * @return action sous forme de tableau de String.
     */
    @Override
    public String[] determinateAction()
    {
        String[] actionToDo;

        Invention bestInvention = Collections.max(inventionProvider.getInventions().stream().filter(i -> !i.isCompleted()).collect(Collectors.toCollection(ArrayList::new)), Comparator.comparing(i -> i.getVictoryPoints()));
        if(player.getFreeInventors().stream().filter(i -> i.canWorkOn(bestInvention)).count() > 0)
        {
            actionToDo = createWorkAction(findBestInventor(bestInvention), bestInvention);
        } else {
            actionToDo = this.createMakeAvalaibleAction();
        }

        return actionToDo;
    }

    /**
     * Détermine une récompense parmi celles proposées.
     * @param avalaibleReward récompenses disponibles.
     * @return récompense choisie.
     */
    @Override
    public Integer determinateReward(ArrayList<Reward> avalaibleReward)
    {
        return avalaibleReward.indexOf(Collections.max(avalaibleReward, Comparator.comparing(r -> r.getValue())));
    }

    /**
     * Retourne le meilleur inventeur du joueur pour travailler sur une invention particulière.
     * @param wannaWorkOnIt invention sur laquelle on veut travailler.
     * @return meilleur inventeur.
     */
    private Inventor findBestInventor(Invention wannaWorkOnIt)
    {
        HashMap<Integer, Inventor> grades = new HashMap<>();

        for(Inventor i : player.getFreeInventors())
            grades.put(i.getGrade(wannaWorkOnIt), i);

        return grades.get(grades.keySet().stream().max(Comparator.naturalOrder()).get());
    }
}
