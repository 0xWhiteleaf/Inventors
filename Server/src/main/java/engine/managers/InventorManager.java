package engine.managers;

import engine.core.Invention;
import engine.core.Inventor;
import engine.core.Knowledges;
import engine.core.Team;
import engine.managers.exceptions.DuplicateInventorException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Classe permettant la gestion des inventeurs.
 * @author Sappa Valentin
 */
public class InventorManager
{
    public static final int IMPLEMENTED_INVENTORS_COUNT = 8;

    /**
     * Classe qui permet de trier les inventeurs en fonction de leur disponibilité.
     */
    private class InventorEntry
    {
        private Inventor inventor;
        private boolean isPicked;

        private InventorEntry(Inventor inventor)
        {
            this.inventor = inventor;
            this.isPicked = false;
        }
    }

    private List<InventorEntry> inventorsEntries;

    public void load() throws DuplicateInventorException {
        synchronized (this)
        {
            //Utilisation d'une liste "thread safe".
            this.inventorsEntries = Collections.synchronizedList(new ArrayList<InventorEntry>());

            //Création des inventeurs...
            this.createInventor("Albert Einstein", new Knowledges(1, 0, 0, 1), Team.Rouge);
            this.createInventor("Thomas Edison", new Knowledges(1, 0, 1, 0), Team.Rouge);
            this.createInventor("Marie Curie", new Knowledges(1, 1, 0, 0), Team.Rouge);
            this.createInventor("Nikola Tesla", new Knowledges(0, 0, 2, 0), Team.Rouge);

            this.createInventor("Blaise Pascal", new Knowledges(0, 0, 1, 1), Team.Jaune);
            this.createInventor("Robert Boyle", new Knowledges(0, 2, 0, 0), Team.Jaune);
            this.createInventor("Galileo Galilei", new Knowledges(1, 1, 0, 0), Team.Jaune);
            this.createInventor("Isaac Newton", new Knowledges(1, 0, 1, 0), Team.Jaune);
        }
    }

    /**
     * Méthode permettant d'initialiser un inventeur et de l'ajouter au dictonnaire des inventeurs disponibles
     * @param name Nom de l'inventeur
     * @param initialKnowledges Connaissances initiales de l'inventeur
     * @throws DuplicateInventorException
     */
    private void createInventor(String name, Knowledges initialKnowledges, Team team) throws DuplicateInventorException
    {
        synchronized (this.inventorsEntries)
        {
            if (this.inventorsEntries.stream()
                    .filter(entry -> entry.inventor.getName() == name)
                    .count() > 0) {
                throw new DuplicateInventorException("Impossible de créer plusieurs inventeurs ayant le même nom.");
            }

            Inventor newInventor = new Inventor(name, initialKnowledges, team);
            this.inventorsEntries.add(new InventorEntry(newInventor));
        }
    }

    /**
     * Fonction permettant de récupérer un inventeur qui n'a pas encore été tirée
     * @return Inventor
     */
    public Inventor pickInventor()
    {
        synchronized (this.inventorsEntries)
        {
            Inventor pickedInventor = null;

            ArrayList<InventorEntry> inventorsNotPicked = this.inventorsEntries.stream()
                    .filter(entry -> !entry.isPicked)
                    .collect(Collectors.toCollection(ArrayList::new));
            if (inventorsNotPicked.size() >= 1) {
                InventorEntry inventorEntry = inventorsNotPicked.get(0);
                inventorEntry.isPicked = true;

                pickedInventor = inventorsNotPicked.get(0).inventor;
            }
            return pickedInventor;
        }
    }

    /**
     * Fonction permettant de récupérer une équipe d'inventeur
     * @param teamColor couleur de l'équipe
     */
    public List<Inventor> pickInventor(Team teamColor)
    {
        synchronized (this.inventorsEntries)
        {
            ArrayList<Inventor> inventorsTeam = new ArrayList<>();

            ArrayList<InventorEntry> inventorsEntries = this.inventorsEntries.stream()
                    .filter(entry -> entry.inventor.getTeam() == teamColor)
                    .collect(Collectors.toCollection(ArrayList::new));

            for (InventorEntry inventorEntry : inventorsEntries)
            {
                inventorEntry.isPicked = true;
                inventorsTeam.add(inventorEntry.inventor);
            }

            return inventorsTeam;
        }
    }

    /**
     * Fonction permettant de récupérer le nombre d'inventeurs disponibles
     * @return Nombre d'inventeurs disponibles
     */
    public int getAvailableInventorsCount()
    {
        int count = 0;
        if(this.inventorsEntries != null)
            count = this.inventorsEntries.size();
        return count;
    }
}