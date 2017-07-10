package engine.core;

/**
 * Classe modélisant les quatre connaissances utilisées à la fois par les inventions et les inventeurs au cours du jeu.
 * La valeur des champs doit être définie après l'instanciation.
 * @author Louis Prud'homme
 */
public class Knowledges
{
    //MEMBERS
    /**
     * Connaissances individuelles
     */
    private int phys;
    private int chem;
    private int mech;
    private int math;

    //METHODS
    /**
     * Constructeur de la classe Knowledges; initialise les champs de l'instance à 0.
     */
    public Knowledges()
    {
        phys = 0;
        chem = 0;
        mech = 0;
        math = 0;
    }

    /**
     * Surchage du constructeur de la classe Knowledges; permet d'initialiser les champs avec une valeur de base.
     * @param basePhys Connaissances de base en physique.
     * @param baseChem Connaissances de base en chimie.
     * @param baseMech Connaissances de base en mécanique.
     * @param baseMath Connaissances de base en mathématiques.
     */
    public Knowledges(int basePhys, int baseChem, int baseMech, int baseMath)
    {
        this.phys = basePhys;
        this.chem = baseChem;
        this.mech = baseMech;
        this.math = baseMath;
    }

    //get-set
    public int getPhys() {
        return phys;
    }

    public int getChem() {
        return chem;
    }

    public int getMech() { return mech; }

    public int getMath() {
        return math;
    }

    /**
     * Permet de modifier la valeur des connaissances en physique en y ajoutant un nombre.
     * @param number nombre qui sera ajouté au champ.
     */
    public void modPhys(int number)
    {
        this.phys += number;
    }

    /**
     * Permet de modifier la valeur des connaissances en chimie en y ajoutant un nombre.
     * @param number nombre qui sera ajouté au champ.
     */
    public void modChem(int number)
    {
        this.chem += number;
    }

    /**
     * Permet de modifier la valeur des connaissances en mecanique en y ajoutant un nombre.
     * @param number nombre qui sera ajouté au champ.
     */
    public void modMech(int number)
    {
        this.mech += number;
    }

    /**
     * Permet de modifier la valeur des connaissances en mathématiques en y ajoutant un nombre.
     * @param number nombre qui sera ajouté au champ.
     */
    public void modMath(int number)
    {
        this.math += number;
    }

    @Override
    public boolean equals(Object object)
    {
        boolean isEqual = false;

        if(object != null && object instanceof Knowledges)
        {
            Knowledges knowledges = (Knowledges)object;
            if(knowledges.getPhys() == this.getPhys() &&
                    knowledges.getChem() == this.getChem() &&
                        knowledges.getMech() == this.getMech() &&
                            knowledges.getMath() == this.getMath())
            {
                isEqual = true;
            }
        }

        return isEqual;
    }
}
