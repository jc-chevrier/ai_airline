package fr.ul.miage.ai_airline;

import fr.ul.miage.ai_airline.build.Builder;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

/**
 * Classe principale du projet.
 */
public class Main {
    /**
     * Lancer l'application.
     */
    public static void main(String[] args) {
        //Construction de l'état de base de la compagnie aérienne.
        Builder.build();

        //Lancement de la plateforme Jade.
        Runtime runtime = Runtime.instance();

        // Configuration du conteneur des agents de la compagnie aérienne
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");           // localhost
        profile.setParameter(Profile.GUI, "true");                      // -gui
        profile.setParameter(Profile.LOCAL_PORT, "9999");               // -port
        profile.setParameter(Profile.CONTAINER_NAME, "avion1");         // -name
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
            e.printStackTrace();
        }
    }

}
