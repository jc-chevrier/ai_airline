package fr.ul.miage.ai_airline.agent;

import fr.ul.miage.ai_airline.Main;
import java.io.IOException;
import java.util.Properties;

/**
 * Lanceur pour le lancement
 * des agents.
 */
public class Starter {
    //Configuration des agents.
    private static Properties configuration;
    //Nom du fichier de configuration.
    public static String CONFIGURATION_FILENAME = "./configuration/agent.properties";

    /**
     * Charger la configuration des agents.
     */
    private static void loadConfiguration() {
        configuration = new Properties();
        try {
            configuration.load(Main.class.getResourceAsStream(CONFIGURATION_FILENAME));
        } catch (IOException e) {
            System.err.println("Erreur ! Problème au cours du chargement de la configuration des agents !");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Lancer les agents.
     */
    public static void start() {
        //Chargement de la configuration.
        if(configuration == null) {
            loadConfiguration();
        }

        //TODO
    }
}
