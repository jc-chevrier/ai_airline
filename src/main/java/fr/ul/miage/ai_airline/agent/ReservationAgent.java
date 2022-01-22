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

/**
 * Agent pour la gestion des requêtes de réservation.
 */
public class ReservationAgent extends Agent {
    //ORM pour l'échange avec la base de données.
    public static ORM orm = ORM.getInstance();

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
                var request = receive();

                //Nouvelle requête de réservation.
                if (request != null) {
                    //Log de debug.
                    System.out.println("[Domaine = compagnie aérienne][Agent = " + getLocalName() + "] " +
                                       "Nouvelle requête de réservation reçue de : " +
                                       request.getSender().getLocalName());

                    //Analyse de la requête, et récupération de ses données.
                    JSONObject JSONRequest = null;
                    Integer resquestId = null, flightId = null,  countAskedPlaces = null;
                    String className = null;
                    try {
                        JSONRequest = new JSONObject(request.getContent());
                        resquestId = JSONRequest.getInt("idRequete");
                        flightId = JSONRequest.getInt("idVol");
                        className = JSONRequest.getString("classe");
                        countAskedPlaces = JSONRequest.getInt("nbPlaces");
                    } catch (JSONException e) {
                        System.err.println("[Domaine = compagnie aérienne][Agent = " + getLocalName() + "] " +
                                           "Problème à l'analyse d'une requête de réservation !");
                        System.err.println(request.getContent());
                        e.printStackTrace();
                    }

                    //Vérification des données de la requête.
                    var correctRequest = true;
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

                    //Création de la réponse.
                    var JSONResponse = new JSONObject();
                    JSONResponse.put("idRequete", resquestId);
                    //Si la requête était correcte.
                    if(correctRequest) {
                        JSONResponse.put("resultat", "Réussite");
                    } else {
                        JSONResponse.put("resultat", "Echec");
                    }

                    //Log de debug.
                    System.out.println("[Domaine = compagnie aérienne][Agent = " + getLocalName() + "] " +
                                        "Envoi d'une réponse:" + JSONResponse.toString());

                    //Envoi de la réponse.
                    var response = request.createReply();
                    //Si la requête était correcte.
                    if(correctRequest) {
                        response.setPerformative(ACLMessage.CONFIRM);
                    } else {
                        response.setPerformative(ACLMessage.FAILURE);
                    }
                    response.setContent(JSONResponse.toString());
                    send(response);
                }
                block();
            }
        });
    }
}