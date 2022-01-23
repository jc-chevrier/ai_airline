package fr.ul.miage.ai_airline.data_structure;

import fr.ul.miage.ai_airline.orm.Entity;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe d'un type d'avion existant
 * dans la compagnie aérienne.
 */
public class PlaneTypeClass extends Entity {
    //Nom de la table correspondant à l'entité.
    public static String NAME_TABLE = "PLANE_TYPE_CLASS";
    //Structure de l'entité [attribut -> type].
    public static Map<String, Class> STRUCTURE;
    //Types de classe existant.
    public final static String FIRST = "Première";
    public final static String BUSINESS = "Business";
    public final static String ECONOMIC = "Economique";


    //Initialisation de la structure.
    static {
        STRUCTURE = new HashMap<String, Class>();
        STRUCTURE.put("ID", Integer.class);
        STRUCTURE.put("NAME", String.class);
        STRUCTURE.put("COUNT_TOTAL_PLACES", Integer.class);
        STRUCTURE.put("PLANE_TYPE_ID", Integer.class);
    }

    public PlaneTypeClass() {
        super();
    }

    public PlaneTypeClass(@NotNull Map<String, Object> attributes) {
        super(attributes);
    }

    public String getName() {
        return (String) get("NAME");
    }

    public void setName(@NotNull String name) {
        set("NAME", name);
    }

    public Integer getCountTotalPlaces() {
        return (Integer) get("COUNT_TOTAL_PLACES");
    }

    public void setCountTotalPlaces(@NotNull Integer countTotalPlaces) {
        set("COUNT_TOTAL_PLACES", countTotalPlaces);
    }

    public Integer getPlaneTypeId() {
        return (Integer) get("PLANE_TYPE_ID");
    }

    public void setPlaneTypeId(@NotNull Integer planeTypeId) {
        set("PLANE_TYPE_ID", planeTypeId);
    }
}