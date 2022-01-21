package fr.ul.miage.ai_airline.data_structure;

import fr.ul.miage.ai_airline.orm.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe d'un vol assuré par la
 * compagnie aérienne.
 */
public class FlightClass extends Entity {
    //Nom de la table correspondant à l'entité.
    public static String NAME_TABLE = "FLIGHT_CLASS";
    //Structure de l'entité [attribut -> type].
    public static Map<String, Class> STRUCTURE;

    //Initialisation de la structure.
    static {
        STRUCTURE = new HashMap<String, Class>();
        STRUCTURE.put("ID", Integer.class);
        STRUCTURE.put("FLOOR_PLACE_PRICE", Double.class);
        STRUCTURE.put("PLACE_PRICE", Double.class);
        STRUCTURE.put("COUNT_AVAILABLE_PLACES", Integer.class);
        STRUCTURE.put("COUNT_OCCUPIED_PLACES", Integer.class);
        STRUCTURE.put("FLIGHT_ID", Integer.class);
        STRUCTURE.put("PLANE_TYPE_CLASS_ID", Integer.class);
    }

    public FlightClass() {
        super();
    }

    public FlightClass(@NotNull Map<String, Object> attributes) {
        super(attributes);
    }

    public Double getFloorPlacePrice() {
        return (Double) get("FLOOR_PLACE_PRICE");
    }

    public void setFloorPlacePrice(@NotNull Double floorPlacePrice) {
        set("FLOOR_PLACE_PRICE", floorPlacePrice);
    }

    public Double getPlacePrice() {
        return (Double) get("PLACE_PRICE");
    }

    public void setPlacePrice(@NotNull Double placePrice) {
        set("PLACE_PRICE", placePrice);
    }

    public Integer getCountAvailablePlaces() {
        return (Integer) get("COUNT_AVAILABLE_PLACES");
    }

    public void setCountAvailablePlaces(@NotNull Integer countFreePlaces) {
        set("COUNT_AVAILABLE_PLACES", countFreePlaces);
    }

    public Integer getCountOccupiedPlaces() {
        return (Integer) get("COUNT_OCCUPIED_PLACES");
    }

    public void setCountOccupiedPlaces(@NotNull Integer countOccupiedPlaces) {
        set("COUNT_OCCUPIED_PLACES", countOccupiedPlaces);
    }

    public Integer getFlightId() {
        return (Integer) get("FLIGHT_ID");
    }

    public void setFlightId(@NotNull Integer flightId) {
        set("FLIGHT_ID", flightId);
    }

    public Integer getPlaneTypeClassId() {
        return (Integer) get("PLANE_TYPE_CLASS_ID");
    }

    public void setPlaneTypeClassId(@NotNull Integer planeTypeClassId) {
        set("PLANE_TYPE_CLASS_ID", planeTypeClassId);
    }
}