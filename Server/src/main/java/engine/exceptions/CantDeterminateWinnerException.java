package engine.exceptions;

/**
 * Classe d'erreur, retournée lorsque le gagnant de la partie n'a pas pu être déterminé.
 * @author Noé Mourton-Comte
 */
public class CantDeterminateWinnerException extends Exception
{
    public CantDeterminateWinnerException(String message)
    {
        super(message);
    }
}
