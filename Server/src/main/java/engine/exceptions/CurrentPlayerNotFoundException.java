package engine.exceptions;

/**
 * Classe d'erreur, retournée lorsque le joueur qui doit jouer le tour courrant n'a pas pu être déterminé.
 * @author Noé Mourton-Comte
 */
public class CurrentPlayerNotFoundException extends Exception
{
    public CurrentPlayerNotFoundException(String message)
    {
        super(message);
    }
}
