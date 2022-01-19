package fr.ul.miage.ai_airline.data_structure;

import fr.ul.miage.ai_airline.orm.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Type d'avion existant dans la
 * compagnie aérienne.
 */
public class PlaneType extends Entity {
    //Nom de la table correspondant à l'entité.
    public static String NAME_TABLE = "PLANE_TYPE";
    //Structure de l'entité [attribut -> type].
    public static Map<String, Class> STRUCTURE;

    //Initialisation de la structure.
    static {
        STRUCTURE = new HashMap<String, Class>();
        STRUCTURE.put("ID", Integer.class);
        STRUCTURE.put("NAME", String.class);
        STRUCTURE.put("SALE_PRICE", Double.class);
        STRUCTURE.put("MAINTENANCE_COST", Double.class);
        STRUCTURE.put("COUNT_TOTAL_PLACES", Integer.class);
    }

    public PlaneType() {
        super();
    }

    public PlaneType(@NotNull Map<String, Object> attributes) {
        super(attributes);
    }

    public String getName() {
        return (String) get("NAME");
    }

    public void setName(@NotNull String name) {
        set("NAME", name);
    }

    public Double getSalePrice() {
        return (Double) get("SALE_PRICE");
    }

    public void setSalePrice(@NotNull Double salePrice) {
        set("SALE_PRICE", salePrice);
    }

    public Double getMaintenanceCost() {
        return (Double) get("MAINTENANCE_COST");
    }

    public void setMaintenanceCost(@NotNull Double maintenanceCost) {
        set("MAINTENANCE_COST", maintenanceCost);
    }

    public Integer getCountTotalPlaces() {
        return (Integer) get("COUNT_PLACES_TOTAL");
    }

    public void setCountTotalPlaces(@NotNull Integer countTotalPlaces) {
        set("COUNT_PLACES_TOTAL", countTotalPlaces);
    }
}