package engine.core.rewards;

import java.util.Random;

/**
 * Classe représentant les pions de récompenses
 * @author Valentin Sappa
 */
public class Reward
{
    private static Random rand = new Random();

    private RewardType type;
    private int value;

    public Reward(RewardType type)
    {
        this.type = type;
        switch (type)
        {
            case VICTORY:
                this.value = rand.nextInt(3) + 1;
                break;
        }
    }

    public Reward(RewardType type, int value)
    {
        this.type = type;
        this.value = value;
    }

    public RewardType getType()
    {
        return this.type;
    }

    public int getValue()
    {
        return this.value;
    }

    @Override
    public String toString()
    {
        return String.format("%s (valeur: %d)", this.type.toString(), this.value);
    }
}
