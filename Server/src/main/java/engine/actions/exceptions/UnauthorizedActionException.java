package engine.actions.exceptions;

/**
 * Classe d'erreur, retournée lorsque qu'un joueur effectue une action non autorisée.
 * @author Noé Mourton-Comte
 */
public class UnauthorizedActionException extends Exception
{
    public UnauthorizedActionException(String message)
    {
        super(message);
    }
}