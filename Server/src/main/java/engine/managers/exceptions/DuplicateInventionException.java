package engine.managers.exceptions;

/**
 * Classe d'erreur, retournée lorsqu'une même invention est "créée" plusieurs fois.
 * @author Noé Mourton-Comte
 */
public class DuplicateInventionException extends Exception
{
    public DuplicateInventionException(String message)
    {
        super(message);
    }
}
