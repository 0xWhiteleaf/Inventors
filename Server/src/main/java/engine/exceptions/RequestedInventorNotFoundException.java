package engine.exceptions;

/**
 * Classe d'erreur, retournée lorsque le joueur essaye de faire jouer un inventeur qu'il ne possède pas.
 * @author Noé Mourton-Comte
 */
public class RequestedInventorNotFoundException extends Exception
{
    public RequestedInventorNotFoundException(String message)
    {
        super(message);
    }
}
