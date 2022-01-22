package fr.ul.miage.ai_airline.agent;

import fr.ul.miage.ai_airline.Main;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

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

        Runtime runtime = Runtime.instance();
        // Configuration du conteneur des agents de la compagnie aérienne
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, (String) configuration.get("main_host"));          // localhost
        profile.setParameter(Profile.GUI, (String) configuration.get("gui"));                      // -gui
        profile.setParameter(Profile.LOCAL_PORT, (String) configuration.get("local_port"));        // -port
        profile.setParameter(Profile.CONTAINER_NAME, (String) configuration.get("container_name"));// -name
        ContainerController containerController = runtime.createMainContainer(profile);

        // Liste des agents a démarrer
        AgentController agentFakeReservationController;                         // agent de demandes de consultations
        AgentController agentFakeConsultationController;                        // agent de demandes de réservations
        AgentController agentReservationController;                             // agent écoutant les réservations
        AgentController agentConsultationController;                            // agent écoutant les consultations
        try {
            // Réservations
            agentFakeReservationController = containerController.createNewAgent("fake_agent_reservation", "fr.ul.miage.ai_airline.agent.FakeReservationRequestAgent", null);
            agentFakeReservationController.start();
            agentReservationController = containerController.createNewAgent("agent_reservation", "fr.ul.miage.ai_airline.agent.ReservationAgent", null);
            agentReservationController.start();
            // Consultations
            agentFakeConsultationController = containerController.createNewAgent("fake_agent_consultation", "fr.ul.miage.ai_airline.agent.FakeConsultationRequestAgent", null);
            agentFakeConsultationController.start();
            agentConsultationController = containerController.createNewAgent("agent_consultation", "fr.ul.miage.ai_airline.agent.ConsultationAgent", null);
            agentConsultationController.start();
        } catch (StaleProxyException e) {
            System.err.println("Erreur lors du démarrage des agents");
            e.printStackTrace();
            System.exit(1);
        }
    }

    }

