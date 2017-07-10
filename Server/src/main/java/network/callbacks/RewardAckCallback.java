package network.callbacks;

import com.corundumstudio.socketio.AckCallback;
import engine.core.rewards.Reward;

import java.util.List;

/**
 * Classe utilisée lors de la demande d'une recompense à un client
 * @author Valentin Sappa
 */
public class RewardAckCallback extends AckCallback<Integer>
{
    private Object locker;
    private List<Reward> rewards;

    private Reward selectedReward = null;

    public RewardAckCallback(Object locker, List<Reward> rewards)
    {
        super(Integer.class, 1);
        this.locker = locker;
        this.rewards = rewards;
    }

    @Override
    public void onSuccess(Integer result) {
        synchronized (this.locker) {
            if (result >= 0 && result < this.rewards.size()) {
                this.selectedReward = this.rewards.get(result);
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

    public Reward getSelectedReward()
    {
        return this.selectedReward;
    }
}

