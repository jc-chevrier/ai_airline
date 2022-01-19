package fr.ul.miage.ai_airline.orm;

import org.jetbrains.annotations.NotNull;
import java.util.*;

/**
 * Outil pour les métadonnées des entités.
 *
 * Cette classe sert à récupérer / extraire les
 * métadonnées des entités : nom de table, structure
 * de tables : leurs attributs.
 */
public class EntityMetadata {
    /**
     * Récupérer le nom de la table de la base de
     * données correspondant à une classe entité.
     *
     * @param classEntity
     * @return
     */
    public static String getTableNameEntity(@NotNull Class classEntity) {
        String tableNameEntity = null;
        try {
            tableNameEntity = (String) classEntity.getDeclaredField("NAME_TABLE").get(String.class);
        } catch (Exception e) {
            System.err.println("Erreur ! La récupération du nom de table d'une entité a echoué, entité : \"" +
                               classEntity.getSimpleName() + "\" !");
            e.printStackTrace();
            System.exit(1);
        }
        return tableNameEntity;
    }

    /**
     * Récupérer la structure d'une entité, c'est-à-dire
     * les métadonnées des attributs de la table qui lui
     * est associé : les noms et les types des attributs.
     *
     * @param classEntity
     * @return
     */
    public static Map<String, Class> getStructureEntity(@NotNull Class classEntity) {
        Map<String, Class> strutureEntity = null;
        try {
            strutureEntity = (Map<String, Class>) classEntity.getDeclaredField("STRUCTURE").get(Map.class);
        } catch (Exception e) {
            System.err.println("Erreur ! La récupération de la structure d'une entité a echoué, entité : \"" +
                               classEntity.getSimpleName() + "\" !");
            e.printStackTrace();
            System.exit(1);
        }
        return strutureEntity;
    }

    /**
     * Créer un nouvel n-uplet d'une entité.
     *
     * @param classEntity
     * @return
     */
    public static Entity instanceEntity(@NotNull Class classEntity,
                                        @NotNull Map<String, Object> attributesEntity) {
        Entity entity = null;
        try {
            entity = (Entity) classEntity.getDeclaredConstructor(Map.class).newInstance(attributesEntity);
        } catch (Exception e) {
            System.err.println("Erreur ! Une instanciation d'un n-uplet à partir de métadonnées a échoué, entité : \"" +
                               classEntity.getSimpleName() + "\", n-uplet : \""+ attributesEntity +"\" !");
            e.printStackTrace();
            System.exit(1);
        }
        return entity;
    }
}