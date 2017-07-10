package network.callbacks;

import engine.core.rewards.Reward;
import engine.core.rewards.RewardType;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Classe destinée à tester la classe "RewardAckCallback".
 * @author Barry Hezam
 */
public class RewardAckCallbackTest
{
    private List<Reward> rewards;

    @Before
    public void init()
    {
        this.rewards = new ArrayList<>();

        Reward reward1 = new Reward(RewardType.CARD, 1);
        Reward reward2 = new Reward(RewardType.VICTORY);

        this.rewards.add(reward1);
        this.rewards.add(reward2);
    }

    @Test
    public void pickCorrectReward()
    {
        RewardAckCallback rewardAckCallback = new RewardAckCallback(new Object(), this.rewards);

        rewardAckCallback.onSuccess(0);
        Reward selectedReward = rewardAckCallback.getSelectedReward();

        assertEquals(selectedReward, this.rewards.get(0));
    }

    @Test
    public void pickIncorrectReward()
    {
        RewardAckCallback rewardAckCallback = new RewardAckCallback(new Object(), this.rewards);

        rewardAckCallback.onSuccess(2);
        Reward selectedReward = rewardAckCallback.getSelectedReward();

        assertNull(selectedReward);
    }
}