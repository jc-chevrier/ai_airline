package fr.ul.miage.ai_airline.orm;

import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Superlasse abstraite des entités.
 */
public abstract class Entity {
    //attributes de l'entité.
    protected Map<String, Object> attributes;

    /**
     * Créer un nouvel n-uplet sur l'application.
     */
    public Entity() {
        Map<String, Class> structure = EntityMetadata.getStructureEntity(getClass());
        attributes = new HashMap<String, Object>();
        for(String attribute : structure.keySet()) {
            set(attribute, null);
        }
    }

    /**
     * Analyser puis convertir en objet un n-uplet de la base
     * de données.
     *
     * (Constructeur utilisé par l'ORM).
     */
    public Entity(@NotNull Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    /**
     * Savoir si le n-uplet a renseigné un attribut.
     */
    public boolean has(@NotNull String attribute) {
        return attributes.containsKey(attribute);
    }

    /**
     * Obtenir un attribut.
     *
     * @param attribute
     * @return
     */
    public Object get(@NotNull String attribute) {
        if(!has(attribute)) {
            throw new IllegalArgumentException("L'attribute " + attribute + " est introuvale !");
        }
        return attributes.get(attribute);
    }

    /**
     * Modifier / ajouter un attribut.
     *
     * @param attribute
     * @param value
     * @return
     */
    public Object set(@NotNull String attribute, Object value) {
        return attributes.put(attribute, value);
    }

    /**
     * Obtenir l'attribute ID.
     *
     * @return
     */
    public Integer getId() {
        return (Integer) get("ID");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return getId().equals(entity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributes);
    }

    @Override
    public String toString() {
        String content = getClass().getSimpleName() + " [ ";
        for(String attribute : attributes.keySet()) {
            Object value = attributes.get(attribute);
            content +=  attribute.toLowerCase() + " = " + value + ", ";
        }
        content = content.substring(0, content.length() - 2) + " ]";
        return content;
    }
}