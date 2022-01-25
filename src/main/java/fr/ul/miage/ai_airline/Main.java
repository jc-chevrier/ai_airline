package fr.ul.miage.ai_airline;

import fr.ul.miage.ai_airline.agent.Starter;
import fr.ul.miage.ai_airline.configuration.Configuration;
import fr.ul.miage.ai_airline.dataset.Builder;

/**
 * Classe principale de la solution.
 */
public class Main {
    /**
     * Lancer la solution.
     */
    public static void main(String[] args) {
        //Récupération de la configuration globale.
        var globalConfiguration = Configuration.GLOBAl_CONFIGURATION;
        var buildDataset = Boolean.parseBoolean(globalConfiguration.getProperty("build_dataset"));

        //Construction de l'état de base de la
        //compagnie aérienne (jeu de données).
        if(buildDataset) {
            Builder.build();
        }

        //Lancement des agents de la compagnie
        //aérienne.
        Starter.start();
    }
}
