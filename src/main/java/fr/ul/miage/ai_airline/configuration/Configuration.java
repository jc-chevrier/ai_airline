package fr.ul.miage.ai_airline.configuration;

import fr.ul.miage.ai_airline.Main;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.Properties;

/**
 * Classe de la configuration de la
 * compagnie aérienne.
 */
public class Configuration {
    //Chemin du fichier de la configuration globale.
    public final static String GLOBAl_CONFIGURATION_FILENAME = "./configuration/global.properties";

    //Chemin du fichier de la configuration de la base de données.
    public final static String DATABASE_CONFIGURATION_FILENAME = "./configuration/database.properties";

    //Chemin du fichier de la configuration des agents.
    public final static String AGENT_CONFIGURATION_FILENAME = "./configuration/agent.properties";

    //Configuration globale.
    public final static Properties GLOBAl_CONFIGURATION = loadConfiguration(GLOBAl_CONFIGURATION_FILENAME);

    //Configuration de la base de données.
    public final static Properties DATABASE_CONFIGURATION = loadConfiguration(DATABASE_CONFIGURATION_FILENAME);

    //Configuration des agents.
    public final static Properties AGENT_CONFIGURATION = loadConfiguration(AGENT_CONFIGURATION_FILENAME);

    /**
     * Charger une configuration.
     *
     * @param configurationFile
     * @return
     */
    public static Properties loadConfiguration(@NotNull String configurationFile) {
        try {
            var configuration = new Properties();
            configuration.load(Main.class.getResourceAsStream(configurationFile));
            return configuration;
        } catch (IOException e) {
            System.err.println("Erreur! Problème au cours du chargement de la configuration des agents!");
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }
}
