package fr.ul.miage.ai_airline.mock_agent;

import fr.ul.miage.ai_airline.data_structure.Flight;
import fr.ul.miage.ai_airline.data_structure.FlightClass;
import fr.ul.miage.ai_airline.data_structure.PlaneTypeClass;
import fr.ul.miage.ai_airline.orm.ORM;
import fr.ul.miage.ai_airline.tool.DateFormatter;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import org.json.JSONObject;
import java.util.Date;

/**
 * Mock pour simuler l'agent de l'assistant client
 * qui envoie des requêtes de réservation de vol.
 */
public class MockReservationRequestAgent extends Agent {
    //ORM pour l'échange avec la base de données.
    public static ORM orm = ORM.getInstance();
    //Séquence des Identifiants de requêtes.
    private static Integer REQUEST_ID = 1;

    @Override
    protected void setup() {
        //Log de debug.
        System.out.println("[Domaine = assistant client] Initialisation d'un nouvel agent mock de requête de réservation : " +
                getLocalName() + " aka " + getAID().getName() + ".");


        addBehaviour(new TickerBehaviour(this, 3000) {
            @Override
            protected void onTick() {
                //Log de debug.
                System.out.println("[Domaine = assistant client][Agent = " + getLocalName() + "] " +
                        "Nouvelle envoi de requête de réservation.");

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
                    JSONRequest.put("dateDemande", DateFormatter.formatAtISO8601(new Date()));
                    JSONRequest.put("idVol", fligthClass.getFlightId());
                    JSONRequest.put("classe", planeTypeClass.getName());
                    JSONRequest.put("nbPlaces", 1);

                    //Log de debug.
                    System.out.println("[Domaine = assistant client][Agent = " + getLocalName() + "] " +
                            "Envoi d'une nouvelle requête:" + JSONRequest.toString());

                    //Envoi de la requête.
                    ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                    request.setContent(JSONRequest.toString());
                    request.addReceiver(new AID("", AID.ISLOCALNAME));
                    send(request);
                } else {
                    //Log de debug.
                    System.out.println("[Domaine = assistant client][Agent = " + getLocalName() + "] " +
                            "Aucune place disponible trouvé à demander.");
                }

                block();
            }
        });

    }
}