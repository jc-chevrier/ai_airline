package fr.ul.miage.ai_airline;

import fr.ul.miage.ai_airline.agent.Starter;
import fr.ul.miage.ai_airline.data_structure.Flight;
import fr.ul.miage.ai_airline.dataset.Builder;
import fr.ul.miage.ai_airline.orm.ORM;

/**
 * Classe principale du projet.
 */
public class Main {
    /**
     * Lancer l'application.
     */
    public static void main(String[] args) {
        //Construction de l'état de base de la
        //compagnie aérienne (jeu de données).
        Builder.build();
        //Lancement des agents de la compagnie
        //aérienne.
        Starter.start();
        ORM.getInstance().findAll(Flight.class).forEach(System.out::println);
    }
}