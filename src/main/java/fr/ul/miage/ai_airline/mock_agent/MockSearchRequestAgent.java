package fr.ul.miage.ai_airline.mock_agent;

import fr.ul.miage.ai_airline.configuration.Configuration;
import fr.ul.miage.ai_airline.orm.ORM;
import fr.ul.miage.ai_airline.tool.DateConverter;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Mock pour simuler l'agent de l'assistant
 * client qui envoie des requêtes de
 * recherche de vol.
 */
public class MockSearchRequestAgent extends Agent {
    //Séquence des Identifiants de requêtes.
    private static Integer REQUEST_ID = 1;

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

        //Log de debug.
        if(debugMode) {
            System.out.println("[Assistant client] Initialisation d'un nouvel agent mock de requête de recherche : " +
                               getLocalName() + " aka " + getAID().getName() + ".");

        }

        //Comportement d'envoi des requêtes de
        //recherche.
        addBehaviour(new CyclicBehaviour() {
             @Override
             public void action() {
                //Log de debug.
                if(debugMode) {
                    System.out.println("[Assistant client][" + getLocalName() + "] Nouvelle envoi de requête de recherche.");
                }

                //Création d'une requête.
                JSONObject JSONRequest = new JSONObject();
                JSONRequest.put("idRequete", REQUEST_ID);
                REQUEST_ID++;
                JSONRequest.put("dateDemande", DateConverter.dateToString(new Date()));
                JSONRequest.put("dateDepart", "2022-01-28 11:30:30");
                JSONRequest.put("prixHaut", "200.0");
                JSONRequest.put("prixBas", "10.0");
                JSONRequest.put("destination", "Barcelone (Espagne)");
                JSONRequest.put("classe", "Economique");

                //Log de debug.
                if(debugMode) {
                    System.out.println("[Assistant client][" + getLocalName() + "] " +
                                       "Envoi d'une nouvelle requête de recherche:" + JSONRequest);
                }

                //Envoi de la requête.
                ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                request.setContent(JSONRequest.toString());
                request.addReceiver(new AID(searchAgentName, AID.ISLOCALNAME));
                send(request);

                block();
            }
        });
    }
}