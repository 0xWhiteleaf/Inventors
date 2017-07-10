package engine.ai.strategies;

import engine.ai.strategies.providers.InventionProvider;
import engine.ai.strategies.providers.UUIDProvider;
import engine.core.*;
import engine.core.rewards.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Classe représentant une stratégie se voulant optimisée pour la victoire; cette stratégie cible les récompenses et essaie d'en avoir le plus possible, en priorisant les plus importantes.
 * @author Louis Prud'homme
 */
public class StealStrategy extends Strategy
{
    /**
     * Constructeur de la stratégie.
     * @param inventionProvider interface fournissant l'accès aux données des inventions en jeu.
     * @param player joueur pour lequel la stratégie va définir les actions.
     */
    public StealStrategy(Player player, UUIDProvider uuidProvider, InventionProvider inventionProvider)
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

        Invention bestInvention = findBestInvention();
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

    /**
     * Permet de trouver la meilleure invention à laquelle le joueur n'a pas contribué, en se basant sur les points de victoire qu'elle octroie au total.
     * Si l'IA a contribué à toutes les inventions, cette fonction retourne la meilleure de toute (toujours sur la base des points de victoire totaux).
     * @return meilleure invention.
     */
    private Invention findBestInvention()
    {
        ArrayList<Invention> avalaibleInventions = inventionProvider.getInventions().stream().filter(i -> !i.isCompleted()).collect(Collectors.toCollection(ArrayList::new));
        avalaibleInventions = avalaibleInventions.stream().filter(i -> !didIContributedOn(i)).collect(Collectors.toCollection(ArrayList::new));

        if(avalaibleInventions.size() > 0)
            return Collections.max(avalaibleInventions, Comparator.comparing(i -> i.getVictoryPoints()));
        else
            return Collections.max(inventionProvider.getInventions().stream().filter(i -> !i.isCompleted()).collect(Collectors.toCollection(ArrayList::new)), Comparator.comparing(i -> i.getVictoryPoints()));
    }

    /**
     * Permet de savoir si l'IA a déjà contribué à une invention ou pas.
     * @return vrai si l'IA a déjà contribué, faux dans le cas contraire.
     */
    private boolean didIContributedOn(Invention invention)
    {
        return invention.getContributions().containsKey(UUID.fromString(uuidProvider.getUUID()));
    }
}
