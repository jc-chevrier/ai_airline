package fr.ul.miage.ai_airline.agent;

import fr.ul.miage.ai_airline.Main;
import fr.ul.miage.ai_airline.mock_agent.MockSearchRequestAgent;
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
            System.err.println("Erreur! Problème au cours du chargement de la configuration des agents !");
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

        //Récupération de la configuration pour le lancement de Jade.
        String host = (String) configuration.get("main_host");
        String isGUI = (String) configuration.get("gui");
        String RMIServerPort = (String) configuration.get("local_port");
        String containerName = (String) configuration.get("container_name");

        //Lancement du conteneur des agents.
        Runtime runtime = Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, host);
        profile.setParameter(Profile.GUI, isGUI);
        profile.setParameter(Profile.LOCAL_PORT, RMIServerPort);
        profile.setParameter(Profile.CONTAINER_NAME, containerName);
        ContainerController containerController = runtime.createMainContainer(profile);

        //Récupération de la configuration pour le lancement des agsnts de Jade.
        String mockReservationAgentName = (String) configuration.get("mock_reservation_name");
        String mockConsultationAgentName = (String) configuration.get("mock_consultation_name");
        String reservationAgentName = (String) configuration.get("reservation_name");
        String consultationAgentName = (String) configuration.get("consultation_name");

        //Lancement des agents.
        AgentController mockReservationAgentController;
        AgentController mockConsultationAgentController;
        AgentController reservationAgentController;
        AgentController consultationAgentController;
        try {
            //Lancement du mock de l'agent de requête de recherche de vol.
            mockConsultationAgentController = containerController.createNewAgent(mockConsultationAgentName,
                                                                                 MockSearchRequestAgent.class.getName(),
                                                                                 null);
            mockConsultationAgentController.start();

            //Lancement de l'agent de recherche de vol.
            consultationAgentController = containerController.createNewAgent(consultationAgentName,
                                                                             SearchAgent.class.getName(),
                                                                             null);
            consultationAgentController.start();

            //Lancement du mock de l'agent de requête de réservation de vol.
            mockReservationAgentController = containerController.createNewAgent(mockReservationAgentName,
                                                                                MockReservationRequestAgent.class.getName(),
                                                                                null);
            mockReservationAgentController.start();

            //Lancement de l'agent de réservation de vol.
            reservationAgentController = containerController.createNewAgent(reservationAgentName,
                                                                            ReservationAgent.class.getName(),
                                                                            null);
            reservationAgentController.start();
        } catch (StaleProxyException e) {
            System.err.println("Erreur lors du démarrage des agents !");
            e.printStackTrace();
            System.exit(1);
        }
    }
}