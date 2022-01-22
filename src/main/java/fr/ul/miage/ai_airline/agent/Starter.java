package fr.ul.miage.ai_airline.agent;

import fr.ul.miage.ai_airline.Main;
import fr.ul.miage.ai_airline.mock_agent.MockConsultationRequestAgent;
import fr.ul.miage.ai_airline.mock_agent.MockReservationRequestAgent;
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
        if (configuration == null) {
            loadConfiguration();
        }
        String host = (String) configuration.get("main_host");
        String isGUI = (String) configuration.get("gui");
        String RMIserverPort = (String) configuration.get("local_port");
        String containerName = (String) configuration.get("container_name");

        Runtime runtime = Runtime.instance();
        // Configuration du conteneur des agents de la compagnie aérienne
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, host);                  // localhost
        profile.setParameter(Profile.GUI, isGUI);                       // -gui
        profile.setParameter(Profile.LOCAL_PORT, RMIserverPort);        // -port
        profile.setParameter(Profile.CONTAINER_NAME, containerName);    // -name
        ContainerController containerController = runtime.createMainContainer(profile);

        // Liste des agents a démarrer
        AgentController agentFakeReservationController;                         // agent de demandes de consultations
        AgentController agentFakeConsultationController;                        // agent de demandes de réservations
        AgentController agentReservationController;                             // agent écoutant les réservations
        AgentController agentConsultationController;                            // agent écoutant les consultations
        String mockReservationName = (String) configuration.get("mock_reservation_name");
        String mockConsultationName = (String) configuration.get("mock_consultation_name");
        String reservationName = (String) configuration.get("reservation_name");
        String consultationName = (String) configuration.get("consultation_name");

        try {
            // Réservations
            agentFakeReservationController = containerController.createNewAgent(mockReservationName, MockReservationRequestAgent.class.getName(), null);
            agentFakeReservationController.start();
            agentReservationController = containerController.createNewAgent(reservationName, ReservationAgent.class.getName(), null);
            agentReservationController.start();
            // Consultations
            agentFakeConsultationController = containerController.createNewAgent(mockConsultationName, MockConsultationRequestAgent.class.getName(), null);
            agentFakeConsultationController.start();
            agentConsultationController = containerController.createNewAgent(consultationName, ConsultationAgent.class.getName(), null);
            agentConsultationController.start();
        } catch (StaleProxyException e) {
            System.err.println("Erreur lors du démarrage des agents");
            e.printStackTrace();
            System.exit(1);
        }
    }
}

