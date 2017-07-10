package engine.actions.exceptions;

/**
 * Classe d'erreur, retournée lorsque qu'un inventeur déjà occupé veut travailler sur une invention.
 * @author Noé Mourton-Comte
 */
public class BusyInventorException extends Exception
{
    public BusyInventorException(String message)
    {
        super(message);
    }
}
