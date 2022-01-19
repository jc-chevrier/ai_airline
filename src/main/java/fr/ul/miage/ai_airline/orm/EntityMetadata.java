package fr.ul.miage.ai_airline.orm;

import org.jetbrains.annotations.NotNull;
import java.util.*;

/**
 * Outil pour les métadonnées des entités.
 *
 * Cette classe sert à récupérer / extraire les
 * métadonnées des entités : nom de table, structure
 * de tables : leurs attributs.
 *
 *
 * @author CHEVRIER, HADJ MESSAOUD, LOUGADI
 */
public class EntityMetadata {
    /**
     * Récupérer le nom de la table de la base de
     * données correspondant à une classe entité.
     *
     * @param entiteClasse
     * @return
     */
    public static String getEntiteNomTable(@NotNull Class entiteClasse) {
        String entiteNomTable = null;
        try {
            entiteNomTable = (String) entiteClasse.getDeclaredField("NOM_TABLE").get(String.class);
        } catch (Exception e) {
            System.err.println("Erreur ! La récupération du nom de table d'une entité a echoué, entité : \"" +
                               entiteClasse.getSimpleName() + "\" !");
            e.printStackTrace();
            System.exit(1);
        }
        return entiteNomTable;
    }

    /**
     * Récupérer la structure d'une entité, c'est-à-dire
     * les métadonnées des attributs de la table qui lui
     * est associé : les noms et les types des attributs.
     *
     * @param entiteClasse
     * @return
     */
    public static Map<String, Class> getEntiteStructure(@NotNull Class entiteClasse) {
        Map<String, Class> entiteStructure = null;
        try {
            entiteStructure = (Map<String, Class>) entiteClasse.getDeclaredField("STRUCTURE").get(Map.class);
        } catch (Exception e) {
            System.err.println("Erreur ! La récupération de la structure d'une entité a echoué, entité : \"" +
                               entiteClasse.getSimpleName() + "\" !");
            e.printStackTrace();
            System.exit(1);
        }
        return entiteStructure;
    }

    /**
     * Créer un nouvel n-uplet d'une entité.
     *
     * @param entiteClasse
     * @return
     */
    public static Entity instancierNUplet(@NotNull Class entiteClasse,
                                          @NotNull Map<String, Object> nUpletAttributs) {
        Entity nUplet = null;
        try {
            nUplet = (Entity) entiteClasse.getDeclaredConstructor(Map.class).newInstance(nUpletAttributs);
        } catch (Exception e) {
            System.err.println("Erreur ! Une instanciation d'un n-uplet à partir de métadonnées a échoué, entité : \"" +
                               entiteClasse.getSimpleName() + "\", n-uplet : \""+ nUpletAttributs +"\" !");
            e.printStackTrace();
            System.exit(1);
        }
        return nUplet;
    }
}