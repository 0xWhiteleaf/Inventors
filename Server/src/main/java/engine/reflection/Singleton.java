package engine.reflection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe représentant un "Singleton" générique.
 * @author Noé Comte
 * @source http://neutrofoton.github.io/blog/2013/08/29/generic-singleton-pattern-in-java/
 */
public class Singleton
{
    private static final Logger log = LoggerFactory.getLogger(Singleton.class);

    private static final Singleton instance = new Singleton();

    @SuppressWarnings("rawtypes")
    private Map<Class, Object> mapHolder = new HashMap<Class, Object>();

    private Singleton() {}

    @SuppressWarnings("unchecked")
    public static <T> T getInstance(Class<T> classOf)
    {
        synchronized(instance){

            if(!instance.mapHolder.containsKey(classOf)){

                T obj = null;

                try {
                    obj = classOf.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                    log.error(String.format("Erreur d'instanciation lors de l'appel de la méthode getInstance<%s>", classOf.getTypeName()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();e.printStackTrace();
                    log.error(String.format("Tentative d'accès non autorisé lors de l'appel de la méthode getInstance<%s>", classOf.getTypeName()));
                }

                instance.mapHolder.put(classOf, obj);
            }

            return (T)instance.mapHolder.get(classOf);

        }
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
