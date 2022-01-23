package fr.ul.miage.ai_airline.mock_agent;

import fr.ul.miage.ai_airline.configuration.Configuration;
import fr.ul.miage.ai_airline.data_structure.FlightClass;
import fr.ul.miage.ai_airline.data_structure.PlaneTypeClass;
import fr.ul.miage.ai_airline.orm.ORM;
import fr.ul.miage.ai_airline.tool.DateConverter;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import org.json.JSONObject;
import java.util.Date;

/**
 * Mock pour simuler l'agent de l'assistant
 * client qui envoie des requêtes de
 * réservation de vol.
 */
public class MockReservationRequestAgent extends Agent {
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
        var reservationAgentName = agentConfiguration.getProperty("reservation_agent_name");

        //Log de debug.
        if(debugMode) {
            System.out.println("[Assistant client] Initialisation d'un nouvel agent mock de requête de réservation : " +
                                getLocalName() + " aka " + getAID().getName() + ".");

        }

        //Comportement d'envoi des requêtes de
        //réservation.
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                //Log de debug.
                if(debugMode) {
                    System.out.println("[Assistant client][" + getLocalName() + "] Nouvelle envoi de requête de réservation.");
                }

                //Récupération des classes de vol avec des places disponibles.
                var availaleFligthClasses = orm.findWhere("WHERE COUNT_AVAILABLE_PLACES > 0", FlightClass.class);

                //Si des places sont disponibles dans une classe d'un vol.
                if (availaleFligthClasses.size() > 0) {
                    //Sélection de la première classe trouvée.
                    var fligthClass = (FlightClass) availaleFligthClasses.get(0);
                    var planeTypeClass = (PlaneTypeClass) orm.findOne(fligthClass.getPlaneTypeClassId(), PlaneTypeClass.class);

                    //Création de la requête.
                    JSONObject JSONRequest = new JSONObject();
                    JSONRequest.put("idRequete", REQUEST_ID);
                    REQUEST_ID++;
                    JSONRequest.put("dateDemande", DateConverter.dateToString(new Date()));
                    JSONRequest.put("idVol", fligthClass.getFlightId());
                    JSONRequest.put("classe", planeTypeClass.getName());
                    JSONRequest.put("nbPlaces", 1);

                    //Log de debug.
                    if(debugMode) {
                        System.out.println("[Assistant client][" + getLocalName() + "] " +
                                           "Envoi d'une nouvelle requête de réservation :" + JSONRequest);
                    }

                    //Envoi de la requête.
                    ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                    request.setContent(JSONRequest.toString());
                    request.addReceiver(new AID(reservationAgentName, AID.ISLOCALNAME));
                    send(request);
                } else {
                    //Log de debug.
                    if(debugMode) {
                        System.out.println("[Assistant client][" + getLocalName() + "] Aucune place disponible trouvée à demander.");
                    }
                }

                block();
            }
        });
    }
}