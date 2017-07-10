package engine.exceptions;

/**
 * Classe d'erreur, retournée lorsque le joueur ne répond plus.
 * @author Noé Mourton-Comte
 */
public class PlayerNotRespondingException extends Exception
{
    public PlayerNotRespondingException(String message)
    {
        super(message);
    }
}
