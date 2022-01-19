package fr.ul.miage.ai_airline.data_structure;

import fr.ul.miage.ai_airline.orm.Entity;
import org.jetbrains.annotations.NotNull;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Vol assuré par la compagnie aérienne.
 */
public class Flight extends Entity {
    //Nom de la table correspondant à l'entité.
    public static String NAME_TABLE = "FLIGHT";
    //Structure de l'entité [attribut -> type].
    public static Map<String, Class> STRUCTURE;

    //Initialisation de la structure.
    static {
        STRUCTURE = new HashMap<String, Class>();
        STRUCTURE.put("ID", Integer.class);
        STRUCTURE.put("START_DATE", Date.class);
        STRUCTURE.put("ARRIVAL_DATE", Date.class);
        STRUCTURE.put("FLOOR_PRICE", Double.class);
        STRUCTURE.put("START_CITY_ID", Integer.class);
        STRUCTURE.put("ARRIVAL_CITY_ID", Integer.class);
        STRUCTURE.put("PLANE_ID", Integer.class);
    }

    public Date getStartDate() {
        return (Date) get("START_DATE");
    }

    public void setStartDate(@NotNull Date startDate) {
        set("START_DATE", startDate);
    }

    public Date getArrivalDate() {
        return (Date) get("ARRIVAL_DATE");
    }

    public void setArrivalDate(@NotNull Date arrivalDate) {
        set("ARRIVAL_DATE", arrivalDate);
    }

    public Double getFloorPrice() {
        return (Double) get("FLOOR_PRICE");
    }

    public void setFloorPrice(@NotNull Double floorPrice) {
        set("FLOOR_PRICE", floorPrice);
    }

    public Integer getStartCityId() {
        return (Integer) get("START_CITY_ID");
    }

    public void setStartCityId(@NotNull Integer startCityId) {
        set("START_CITY_ID", startCityId);
    }

    public Integer getArrivalCityId() {
        return (Integer) get("ARRIVAL_CITY_ID");
    }

    public void setArrivalCityId(@NotNull Integer arrivalCityId) {
        set("ARRIVAL_CITY_ID", arrivalCityId);
    }

    public Integer getPlaneId() {
        return (Integer) get("PLANE_ID");
    }

    public void setPlaneId(@NotNull Integer planeId) {
        set("PLANE_ID", planeId);
    }
}
