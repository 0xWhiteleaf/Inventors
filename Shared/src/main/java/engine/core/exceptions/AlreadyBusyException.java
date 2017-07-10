package engine.core.exceptions;

/**
 * Classe d'erreur, retournée lors de la l'appel de la méthode "makeBusy", si l'inventeur en question est déjà occupé.
 * @author Noé Mourton-Comte
 */
public class AlreadyBusyException extends Exception
{
    public AlreadyBusyException(String message)
    {
        super(message);
    }
}
