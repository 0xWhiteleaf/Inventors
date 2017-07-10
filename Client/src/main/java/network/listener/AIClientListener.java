package network.listener;

/**
 * Interface d'un écouteur du serveur.
 * @author Noé Mourton-Comte
 */
public interface AIClientListener
{
    void onGameEnded(boolean victory);
    void onGameClosed();
}
