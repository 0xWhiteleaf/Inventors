package network.listener;

import engine.ai.AIPlayer;
import io.socket.emitter.Emitter;
import network.AIClient;

/**
 * Classe représentant un écouteur "injectable" par des objets mockés.
 * @author Noé Mourton-Comte
 */
public abstract class InjectableListener implements Emitter.Listener
{
    private AIClient aiClient;
    private AIPlayer aiPlayer;
    private boolean isInjected = false;

    public InjectableListener(AIClient aiClient, AIPlayer aiPlayer)
    {
        this.aiClient = aiClient;
        this.aiPlayer = aiPlayer;
    }

    public InjectableListener(AIClient aiClient, AIPlayer aiPlayer, boolean isInjected)
    {
        this.aiClient = aiClient;
        this.aiPlayer = aiPlayer;
        this.isInjected = isInjected;
    }

    @Override
    public void call(Object... objects) { }
}
