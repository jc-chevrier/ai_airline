package fr.ul.miage.ai_airline.agent;

import fr.ul.miage.ai_airline.data_structure.Flight;
import fr.ul.miage.ai_airline.data_structure.FlightClass;
import fr.ul.miage.ai_airline.data_structure.PlaneTypeClass;
import fr.ul.miage.ai_airline.orm.Entity;
import fr.ul.miage.ai_airline.orm.ORM;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import org.json.JSONException;
import org.json.JSONObject;

public class ReservationAgent extends Agent {
    @Override
    protected void setup() {
        //Log de debug.
        System.out.println("[Domaine = compagnie aérienne] Initialisation d'un nouvel agent de réservation : " +
                            getLocalName() + " aka " + getAID().getName() + ".");

        //Comportement d'écoute des requêtes de réservations
        addBehaviour(new TickerBehaviour(this, 1000) {
            @Override
            protected void onTick() {
                //Log de debug.
                System.out.println("[Domaine = compagnie aérienne][Agent = " + getLocalName() + "] " +
                                   "Nouvelle écoute des requêtes de réservation.");

                //Attente d'une nouvelle requête de réservation.
                var reservationRequest = receive();

                //Nouvelle requête de réservation.
                if (reservationRequest != null) {
                    //Log de debug.
                    System.out.println("[Domaine = compagnie aérienne][Agent = " + getLocalName() + "] " +
                                       "Nouvelle requête de réservation reçue de : " +
                                       reservationRequest.getSender().getLocalName());

                    //Analyse de la requête, et récupération de ses données.
                    JSONObject JSONRequest = null;
                    Integer resquestId = null, flightId = null,  countAskedPlaces = null;
                    String className = null;
                    try {
                        JSONRequest = new JSONObject(reservationRequest.getContent());
                        resquestId = JSONRequest.getInt("idRequete");
                        flightId = JSONRequest.getInt("idVol");
                        className = JSONRequest.getString("classe");
                        countAskedPlaces = JSONRequest.getInt("nbPlaces");
                    } catch (JSONException e) {
                        System.err.println("[Domaine = compagnie aérienne][Agent = " + getLocalName() + "]" +
                                           " Problème à l'écoute d'une requête de réservation !");
                        System.err.println(reservationRequest.getContent());
                        e.printStackTrace();
                    }

                    //Vérification des données de la requête.
                    var correctRequest = true;
                    var orm = ORM.getInstance();
                    var flight = (Entity) orm.findOne(flightId, Flight.class);
                    //Si le vol existe.
                    if(flight != null) {
                        var flightClasses = orm.findWhere("WHERE FLIGHT_ID = " + flight.getId(), Flight.class);
                        FlightClass flightClass = null;
                        PlaneTypeClass planeTypeClass = null;
                        for(var entity : flightClasses) {
                            flightClass = (FlightClass) entity;
                            planeTypeClass = (PlaneTypeClass) orm.findOne(flightClass.getPlaneTypeClassId(), PlaneTypeClass.class);
                            if(planeTypeClass.getName().equals(className)) {
                                break;
                            }
                        }
                        //Si la classe existe pour le vol.
                        if(planeTypeClass.getName().equals(className)) {
                            //Si le nombre de places demandé est disponible.
                            if(flightClass.hasCountAvailablePlaces(countAskedPlaces)) {
                                //Modification des nombres de places disponibles et occupées.
                                flightClass.incrementPlaces(-countAskedPlaces);
                                //Sauvegarde des modifications.
                                orm.save(flightClass);
                            } else {
                                correctRequest = false;
                            }
                        } else {
                            correctRequest = false;
                        }
                    } else {
                        correctRequest = false;
                    }

                    //Réponse à la requête.
                    var response = reservationRequest.createReply();
                    var JSONResponse = new JSONObject();
                    JSONResponse.put("idRequete", resquestId);

                    //Objet JSON de réponse.
                    //Si la requête était correcte.
                    if(correctRequest) {
                        response .setPerformative(ACLMessage.CONFIRM);
                        JSONResponse.put("resultat", "Réussite");
                    } else {
                        response .setPerformative(ACLMessage.FAILURE);
                        JSONResponse.put("resultat", "Echec");
                    }
                    response.setContent(JSONResponse.toString());
                    send(response);
                }
                block();
            }
        });
    }
}