package engine.ai.strategies.exceptions;

/**
 * Classe d'erreur retournée lorsqu'aucun inventeur ne peut travailler sur aucune invention.
 */
public class NoOneCanWorkException extends Exception
{
    public NoOneCanWorkException(String msg)
    {
        super(msg);
    }
}
