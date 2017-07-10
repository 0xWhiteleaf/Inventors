package engine.ai.strategies;

import engine.ai.strategies.providers.InventionProvider;
import engine.ai.strategies.providers.UUIDProvider;
import engine.core.Invention;
import engine.core.Inventor;
import engine.core.Player;
import engine.core.rewards.Reward;
import network.ProtocolTypes;

import java.util.ArrayList;

/**
 * Class abstraite servant à l'implémentation des stratégies.
 * @author Louis Prud'homme
 */
public abstract class Strategy
{
    protected InventionProvider inventionProvider;
    protected UUIDProvider uuidProvider;
    protected Player player;

    public Strategy(Player player, UUIDProvider uuidProvider, InventionProvider inventionProvider)
    {
        this.player = player;
        this.uuidProvider = uuidProvider;
        this.inventionProvider = inventionProvider;
    }

    /**
     * Permet d'obtenir une action déterminée pour le joueur cible.
     * @return action déterminée au hasard.
     */
    public abstract String[] determinateAction();

    /**
     * Permet d'obtenir de l'IA qu'elle choisisse une récompense.
     * @param avalaibleRewards récompenses disponibles
     * @return récompense choisie
     */
    public abstract Integer determinateReward(ArrayList<Reward> avalaibleRewards);

    /**
     * Permet de créer rapidement une makeAvailaibleAction sous forme de tableau de String (prêt à l'envoi au serveur)
     * @return makeAvailaibleAction sous forme de tableau de String.
     */
    protected String[] createMakeAvalaibleAction()
    {
        String[] makeAvalaibleAction = {ProtocolTypes.MakeAvailableAction.name()};
        return makeAvalaibleAction;
    }

    /**
     * Permet de créer rapidement une workAction sous forme de tableau de String (prêt à l'envoi au serveur)
     * @param inventor inventeur qui va travailler.
     * @param invention invention sur laquelle l'inventeur va travailler.
     * @return workAction sous forme de tableau de String.
     */
    protected String[] createWorkAction(Inventor inventor, Invention invention)
    {
        String[] workAction = {ProtocolTypes.WorkAction.name(), inventor.getName(), invention.getName()};
        return workAction;
    }
}
