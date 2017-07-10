package engine.exceptions;

/**
 * Classe d'erreur, retournée lorsque le joueur essaye d'intéragir avec une invention qui n'existe pas.
 * @author Noé Mourton-Comte
 */
public class RequestedInventionNotFoundException extends Exception
{
    public RequestedInventionNotFoundException(String message)
    {
        super(message);
    }
}
