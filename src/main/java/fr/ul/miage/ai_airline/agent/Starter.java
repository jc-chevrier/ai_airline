package fr.ul.miage.ai_airline.agent;

import fr.ul.miage.ai_airline.configuration.Configuration;
import fr.ul.miage.ai_airline.mock_agent.MockReservationRequestAgent;
import fr.ul.miage.ai_airline.mock_agent.MockSearchRequestAgent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

/**
 * Lanceur pour le lancement
 * des agents.
 */
public class Starter {
    /**
     * Lancer les agents.
     */
    public static void start() {
        //Récupération de la configuration.
        var configuration = Configuration.AGENT_CONFIGURATION;

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
        String mockReservationAgentName = (String) configuration.get("mock_reservation_agent_name");
        String mockSearchAgentName = (String) configuration.get("mock_search_agent_name");
        String reservationAgentName = (String) configuration.get("reservation_agent_name");
        String searchAgentName = (String) configuration.get("search_agent_name");
        String cancelAgentName = (String) configuration.get("canceller_agent_name");

        //Lancement des agents.
        AgentController mockReservationAgentController;
        AgentController mockSearchAgentController;
        AgentController reservationAgentController;
        AgentController searchAgentController;
        AgentController cancelAgentController;
        try {
            //Lancement de l'agent d'annulation de vols vides.
            cancelAgentController = containerController.createNewAgent(cancelAgentName,
                    CancellerAgent.class.getName(),
                    null);
            cancelAgentController.start();
            //Lancement du mock de l'agent de requête de recherche de vol.
            mockSearchAgentController = containerController.createNewAgent(mockSearchAgentName,
                                                                           MockSearchRequestAgent.class.getName(),
                                                                           null);
            mockSearchAgentController.start();
            //Lancement de l'agent de recherche de vol.
            searchAgentController = containerController.createNewAgent(searchAgentName,
                                                                       SearchAgent.class.getName(),
                                                                       null);
            searchAgentController.start();
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
            System.err.println("Erreur! Problème au cours du démarrage des agents!");
            e.printStackTrace();
            System.exit(1);
        }
    }
}