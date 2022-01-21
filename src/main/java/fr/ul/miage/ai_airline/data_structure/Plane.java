package fr.ul.miage.ai_airline.data_structure;

import fr.ul.miage.ai_airline.orm.Entity;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * Avion de la compagnie aérienne.
 */
public class Plane extends Entity {
    //Nom de la table correspondant à l'entité.
    public static String NAME_TABLE = "PLANE";
    //Structure de l'entité [attribut -> type].
    public static Map<String, Class> STRUCTURE;

    //Initialisation de la structure.
    static {
        STRUCTURE = new HashMap<String, Class>();
        STRUCTURE.put("ID", Integer.class);
        STRUCTURE.put("PLANE_TYPE_ID", Integer.class);
    }

    public Plane() {
        super();
    }

    public Plane(@NotNull Map<String, Object> attributes) {
        super(attributes);
    }

    public Integer getPlaneTypeId() {
        return (Integer) get("PLANE_TYPE_ID");
    }

    public void setPlaneTypeId(@NotNull Integer planeTypeId) {
        set("PLANE_TYPE_ID", planeTypeId);
    }
}
