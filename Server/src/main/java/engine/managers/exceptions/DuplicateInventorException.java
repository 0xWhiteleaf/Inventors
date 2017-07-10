package engine.managers.exceptions;

/**
 * Classe d'erreur, retournée lorsqu'une même inventeur est "crée" plusieurs fois.
 * @author Noé Mourton-Comte
 */
public class DuplicateInventorException extends Exception
{
    public DuplicateInventorException(String message)
    {
        super(message);
    }
}
