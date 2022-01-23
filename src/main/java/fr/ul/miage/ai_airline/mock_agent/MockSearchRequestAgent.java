package fr.ul.miage.ai_airline.mock_agent;

import fr.ul.miage.ai_airline.configuration.Configuration;
import fr.ul.miage.ai_airline.orm.ORM;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Mock pour simuler l'agent de l'assistant
 * client qui envoie des requêtes de
 * recherche de vol.
 */
public class MockSearchRequestAgent extends Agent {
    @Override
    protected void setup() {
        //Récupération de l'ORM pour l'interrogation
        //de la base de données.
        var orm = ORM.getInstance();

        //Récupération de la configuration globale.
        var globalConfiguration = Configuration.GLOBAl_CONFIGURATION;
        var debugMode = Boolean.parseBoolean(globalConfiguration.getProperty("debug_mode"));

        //Récupération de la configuration des agents.
        var agentConfiguration = Configuration.AGENT_CONFIGURATION;
        var searchAgentName = agentConfiguration.getProperty("search_agent_name");

        System.out.println("Je un agent qui envoie des requêtes de consultation de vols : " +
                            getLocalName() + " appelé aussi " + getAID().getName());

        addBehaviour(new TickerBehaviour(this, 3000) {
            @Override
            protected void onTick() {
                System.out.println("Fake consultation request agent === Sending message to ");

                String str = "2022-01-28 11:30:30";
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
                String dateDecollage = dateTime.format(formatter);
                String flightrequest =
                        "{"+
                                "\"idRequete\": \"878\","+     // identifiant de requete
                                "\"dateDepart\": \""+dateDecollage+"\","+         // date de départ souhaitée
                                "\"prixHaut\": \"200.00\","+             // fourchette de prix
                                "\"prixBas\": \"10.00\","+             // fourchette de prix
                                "\"destination\": \"Barcelone (Espagne)\","+      // destination souhaitée (soit thème soit destination)
                                "\"classe\": \"Economique\""+            // classe souhaité (1er classe, éco)
                                "}";

                // Send response
                ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                request.setContent(flightrequest);
                request.addReceiver(new AID(searchAgentName, AID.ISLOCALNAME));
                send(request);

                block();
            }
        });
    }
}