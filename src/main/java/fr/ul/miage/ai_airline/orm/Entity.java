package fr.ul.miage.ai_airline.orm;

import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Superlasse abstraite des entités.
 *
 * @author CHEVRIER, HADJ MESSAOUD, LOUGADI
 */
public abstract class Entity {
    //Attributs de l'entité.
    protected Map<String, Object> attributs;

    /**
     * Créer un nouvel n-uplet sur l'application.
     */
    public Entity() {
        Map<String, Class> structure = EntityMetadata.getEntiteStructure(getClass());
        attributs = new HashMap<String, Object>();
        for(String attribut : structure.keySet()) {
            set(attribut, null);
        }
    }

    /**
     * Analyser puis convertir en objet un n-uplet de la base
     * de données.
     *
     * (Constructeur utilisé par l'ORM).
     */
    public Entity(@NotNull Map<String, Object> attributs) {
        this.attributs = attributs;
    }

    /**
     * Savoir si le n-uplet a renseigné un attribut.
     */
    public boolean renseigne(@NotNull String attribut) {
        return attributs.containsKey(attribut);
    }

    /**
     * Obtenir un attribut.
     *
     * @param attribut
     * @return
     */
    public Object get(@NotNull String attribut) {
        if(!renseigne(attribut)) {
            throw new IllegalArgumentException("L'attribut " + attribut + " est introuvale !");
        }
        return attributs.get(attribut);
    }

    /**
     * Modifier / ajouter un attribut.
     *
     * @param attribut
     * @param valeur
     * @return
     */
    public Object set(@NotNull String attribut, Object valeur) {
        return attributs.put(attribut, valeur);
    }

    /**
     * Obtenir l'attribut ID.
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
        Entity entite = (Entity) o;
        return getId().equals(entite.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributs);
    }

    @Override
    public String toString() {
        String contenu = getClass().getSimpleName() + " [ ";
        for(String attribut : attributs.keySet()) {
            Object valeur = attributs.get(attribut);
            contenu +=  attribut.toLowerCase() + " = " + valeur + ", ";
        }
        contenu = contenu.substring(0, contenu.length() - 2) + " ]";
        return contenu;
    }
}