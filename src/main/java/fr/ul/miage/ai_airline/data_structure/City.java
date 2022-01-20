package fr.ul.miage.ai_airline.data_structure;

import fr.ul.miage.ai_airline.orm.Entity;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * Ville desservie par la compagnie aérienne.
 */
public class City extends Entity {
    //Nom de la table correspondant à l'entité.
    public static String NAME_TABLE = "CITY";
    //Structure de l'entité [attribut -> type].
    public static Map<String, Class> STRUCTURE;

    //Initialisation de la structure.
    static {
        STRUCTURE = new HashMap<String, Class>();
        STRUCTURE.put("ID", Integer.class);
        STRUCTURE.put("NAME", String.class);
        STRUCTURE.put("DISTANCE_TO_PARIS", Double.class);
        STRUCTURE.put("TIME_TO_PARIS", Double.class);
    }

    public City() {
        super();
    }

    public City(@NotNull Map<String, Object> attributes) {
        super(attributes);
    }

    public String getName() {
        return (String) get("NAME");
    }

    public void setName(@NotNull String name) {
        set("NAME", name);
    }

    public Double getDistanceToParis() {
        return (Double) get("DISTANCE_TO_PARIS");
    }

    public void setDistanceToParis(@NotNull Double distanceToParis) {
        set("DISTANCE_TO_PARIS", distanceToParis);
    }

    public Double getTimeToParis() {
        return (Double) get("TIME_TO_PARIS");
    }

    public void setTimeToParis(@NotNull Double distanceToParis) {
        set("TIME_TO_PARIS", distanceToParis);
    }
}