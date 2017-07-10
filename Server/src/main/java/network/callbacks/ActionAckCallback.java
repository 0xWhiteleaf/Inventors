package network.callbacks;

import com.corundumstudio.socketio.AckCallback;
import engine.Game;
import engine.actions.Action;
import engine.actions.MakeAvailableAction;
import engine.actions.WorkAction;
import engine.core.Invention;
import engine.core.Inventor;
import engine.core.Player;
import engine.exceptions.RequestedInventionNotFoundException;
import engine.exceptions.RequestedInventorNotFoundException;
import network.ProtocolTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe utilisée lors de la demande d'une action à un client
 * @author Noé Mourton-Comte
 */
public class ActionAckCallback extends AckCallback<String[]>
{
    private static final Logger log = LoggerFactory.getLogger(ActionAckCallback.class);

    private Object locker;
    private Game gameInstance;
    private Player player;

    private Action action;

    public ActionAckCallback(Object locker, Game gameInstance, Player player)
    {
        super(String[].class, 1);
        this.locker = locker;
        this.gameInstance = gameInstance;
        this.player = player;
    }

    @Override
    public void onSuccess(String[] result)
    {
        synchronized (this.locker)
        {
                switch (ProtocolTypes.valueOf(result[0]))
                {
                    case MakeAvailableAction:
                        this.action = new MakeAvailableAction(this.player);
                        break;

                    case WorkAction:
                        try {
                            Inventor inventor = this.gameInstance.getPlayerInventorByName(player, result[1].toString());
                            Invention invention = this.gameInstance.getInventionByName(result[2].toString());
                            this.action = new WorkAction(player, inventor, invention);
                        } catch (RequestedInventorNotFoundException ex) {
                            log.error(ex.getMessage());
                        } catch (RequestedInventionNotFoundException ex) {
                            log.error(ex.getMessage());
                        }
                        break;
                }
            this.locker.notify();
        }
    }

    @Override
    public void onTimeout()
    {
        synchronized (this.locker)
        {
            this.locker.notify();
        }
    }

    /**
     * Fonction retournant l'action instanciée lors de l'appel à la méthode onSuccess
     */
    public Action getAction()
    {
        return this.action;
    }
}
