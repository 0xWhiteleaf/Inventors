package engine.actions.exceptions;

/**
 * Classe d'erreur, retournée lorsque qu'un inventeur "incompatible" veut travailler sur une invention.
 * @author Noé Mourton-Comte
 */
public class IncompatibleInventorException extends Exception
{
    public IncompatibleInventorException(String message)
    {
        super(message);
    }
}
