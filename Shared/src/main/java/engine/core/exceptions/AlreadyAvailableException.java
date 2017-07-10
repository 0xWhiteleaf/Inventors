package engine.core.exceptions;

/**
 * Classe d'erreur, retournée lors de la l'appel de la méthode "makeAvailable", si l'inventeur en question est déjà disponible.
 * @author Noé Mourton-Comte
 */
public class AlreadyAvailableException extends Exception
{
    public AlreadyAvailableException(String message)
    {
        super(message);
    }
}
