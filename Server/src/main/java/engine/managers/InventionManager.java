package engine.managers;

import engine.core.Invention;
import engine.core.Knowledges;
import engine.managers.exceptions.DuplicateInventionException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Classe permettant la gestion des inventions.
 * @author Barry Hezam, Alexandre Ciarafoni
 */
public class InventionManager
{
    public static final int IMPLEMENTED_INVENTIONS_COUNT = 15;

    /**
     * Classe permettant de trier les inventions en fonction de leur disponibilité.
     */
    private class InventionEntry
    {
        private Invention invention;
        private boolean isPicked;

        private InventionEntry(Invention invention)
        {
            this.invention = invention;
            this.isPicked = false;
        }
    }

    private List<InventionEntry> inventionsEntries;

    public void load() throws DuplicateInventionException
    {
        synchronized (this)
        {
            //Utilisation d'une liste "thread safe".
            this.inventionsEntries = Collections.synchronizedList(new ArrayList<InventionEntry>());

            //Création des inventions...

            //Époque 1
            this.createInvention("Feu", new Knowledges(3, 1, 0, 1), 1);
            this.createInvention("Arc", new Knowledges(1, 0, 2, 2), 1);
            this.createInvention("Hâche", new Knowledges(2, 0, 2, 1), 1);
            this.createInvention("Canoë", new Knowledges(2, 0, 1, 2), 1);
            this.createInvention("Signaux de fumées", new Knowledges(1, 3, 0, 1), 1);

            //Époque 2
            this.createInvention("Arbalète médiévale", new Knowledges(1, 0, 4, 2), 2);
            this.createInvention("Pistolet", new Knowledges(2, 3, 1, 1), 2);
            this.createInvention("Montgolfière", new Knowledges(1, 3, 1, 2), 2);
            this.createInvention("Caravelle", new Knowledges(1, 1, 3, 2), 2);
            this.createInvention("Feu d'artifice", new Knowledges(3, 4, 0, 0), 2);

            //Époque 3
            this.createInvention("Mitrailleuse Gatling", new Knowledges(1, 2, 4, 2), 3);
            this.createInvention("Revolver", new Knowledges(1, 2, 3, 3), 3);
            this.createInvention("Automobile", new Knowledges(1, 3, 4, 1), 3);
            this.createInvention("Zeppelin", new Knowledges(2, 4, 1, 2), 3);
            this.createInvention("Téléphone", new Knowledges(3, 1, 2, 3), 3);
        }
    }

    /**
     * Méthode permettant d'initialiser une invention et de l'ajouter au dictonnaire des inventions disponibles
     * @param name Nom de l'invention
     * @param requiredKnowledges Connaissances requises pour terminer l'invention
     * @param era Epoque de l'invention
     * @throws DuplicateInventionException
     */
    private void createInvention(String name, Knowledges requiredKnowledges, int era) throws DuplicateInventionException
    {
        synchronized (this.inventionsEntries)
        {
            if (this.inventionsEntries.stream()
                    .filter(entry -> entry.invention.getName() == name)
                    .count() > 0) {
                throw new DuplicateInventionException("Impossible de créer plusieurs inventions ayant le même nom.");
            }

            Invention newInvention = new Invention(name, requiredKnowledges, era);
            this.inventionsEntries.add(new InventionEntry(newInvention));
        }
    }

    /**
     * Fonction permettant de récupérer une invention qui n'a pas encore été tirée
     * @return Invention
     */
    public Invention pickInvention()
    {
        synchronized (this.inventionsEntries)
        {
            Invention pickedInvention = null;

            ArrayList<InventionEntry> inventionsNotPicked = this.inventionsEntries.stream()
                    .filter(entry -> !entry.isPicked)
                    .collect(Collectors.toCollection(ArrayList::new));
            if (inventionsNotPicked.size() >= 1) {
                InventionEntry inventionEntry = inventionsNotPicked.get(0);
                inventionEntry.isPicked = true;

                pickedInvention = inventionsNotPicked.get(0).invention;
            }

            return pickedInvention;
        }
    }

    /**
     * Fonction permettant de récupérer une invention qui n'a pas encore été tirée
     * @param era époque de l'invention
     * @return nouvelle invention.
     */
    public Invention pickInvention(int era)
    {
        synchronized (this.inventionsEntries)
        {
            Invention pickedInvention = null;

            ArrayList<InventionEntry> inventionsNotPickedForEra = this.inventionsEntries.stream()
                    .filter(entry -> !entry.isPicked && entry.invention.getEra() == era)
                    .collect(Collectors.toCollection(ArrayList::new));
            if (inventionsNotPickedForEra.size() >= 1) {
                InventionEntry inventionEntry = inventionsNotPickedForEra.get(0);
                inventionEntry.isPicked = true;

                pickedInvention = inventionsNotPickedForEra.get(0).invention;
            }

            return pickedInvention;
        }
    }

    public int getAvalaibleInventionsCount()
    {
        int count = 0;

        if(this.inventionsEntries != null)
            count = this.inventionsEntries.size();

        return count;
    }

}