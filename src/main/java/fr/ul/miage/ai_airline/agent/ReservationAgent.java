package fr.ul.miage.ai_airline.agent;

import fr.ul.miage.ai_airline.configuration.Configuration;
import fr.ul.miage.ai_airline.data_structure.Flight;
import fr.ul.miage.ai_airline.data_structure.FlightClass;
import fr.ul.miage.ai_airline.data_structure.Global;
import fr.ul.miage.ai_airline.data_structure.PlaneTypeClass;
import fr.ul.miage.ai_airline.orm.Entity;
import fr.ul.miage.ai_airline.orm.ORM;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Agent pour la gestion des requêtes
 * de réservation.
 */
public class ReservationAgent extends Agent {
    @Override
    protected void setup() {
        //Récupération de l'ORM pour l'interrogation
        //de la base de données.
        var orm = ORM.getInstance();

        //Récupération de la configuration globale.
        var globalConfiguration = Configuration.GLOBAl_CONFIGURATION;
        var debugMode = Boolean.parseBoolean(globalConfiguration.getProperty("debug_mode"));

        //Récupération du contexte global.
        Global global = (Global) orm.findOne(1, Global.class);

        //Log de debug.
        if(debugMode) {
            System.out.println("[Compagnie aérienne] Initialisation d'un nouvel agent de réservation: " +
                               getLocalName() + " aka " + getAID().getName() + ".");
        }

        //Comportement d'écoute et de gestion
        //des requêtes de réservation.
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                //Log de debug.
                if(debugMode) {
                    System.out.println("[Compagnie aérienne][Agent = " + getLocalName() + "] " +
                                       "Nouvelle écoute des requêtes de réservation.");
                }

                //Attente d'une nouvelle requête de réservation.
                var messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
                var request = receive(messageTemplate);

                //Nouvelle requête de réservation.
                if (request != null) {
                    //Log de debug.
                    if(debugMode) {
                        System.out.println("[Compagnie aérienne][Agent = " + getLocalName() + "] " +
                                           "Nouvelle requête de réservation reçue de: " +
                                           request.getSender().getLocalName() + ".");
                    }

                    //Analyse de la requête et extraction de ses données.
                    JSONObject JSONRequest = null;
                    Integer requestId = null, flightId = null,  countAskedPlaces = null;
                    String className = null;
                    try {
                        //Analyse de contenu de la requête.
                        JSONRequest = new JSONObject(request.getContent());
                        //Log de debug.
                        if(debugMode) {
                            System.out.println("[Compagnie aérienne][Agent = " + getLocalName() + "] " +
                                               "Contenu de la requête de réservation reçue: " + JSONRequest + "!");
                        }
                        //Extraction des données du contenu de la requête reçue.
                        requestId = JSONRequest.getInt("idRequete");
                        flightId = JSONRequest.getInt("idVol");
                        className = JSONRequest.getString("classe");
                        countAskedPlaces = JSONRequest.getInt("nbPlaces");
                    } catch (JSONException e) {
                        System.err.println("[Compagnie aérienne][Agent = " + getLocalName() + "] " +
                                           "Erreur! Problème à l'analyse d'une requête de réservation : " + request.getContent() + "!");
                        e.printStackTrace();
                        System.exit(1);
                    }

                    //Vérification des données de la requête
                    //et exécution de la requête si vérification
                    //réussie.
                    var correctRequest = true;
                    var flight = (Entity) orm.findOne(flightId, Flight.class);
                    //Si le vol existe.
                    if(flight != null) {
                        var flightClasses = orm.findWhere("WHERE FLIGHT_ID = " + flight.getId(), FlightClass.class);
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
                            //Si le nombre de places demandé est
                            //disponible.
                            if(flightClass.hasCountAvailablePlaces(countAskedPlaces)) {
                                //Modification des nombres de places
                                //disponibles et occupées.
                                flightClass.incrementPlaces(-countAskedPlaces);
                                //Augmentation du solde de la compagnie avec le gain.
                                global.incrementBalance(flightClass.getPlacePrice() * countAskedPlaces);
                                //Sauvegarde des modifications.
                                orm.save(flightClass);
                                orm.save(global);
                                //Log de debug.
                                if(debugMode) {
                                    System.out.println("[Compagnie aérienne][Agent = " + getLocalName() + "] " +
                                                       "Réservation de places d'une classe faite: " + new JSONObject(flightClass)
                                                        + " " + new JSONObject(global) + ".");
                                }
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
                    JSONResponse.put("idRequete", requestId);
                    //Si la requête était correcte.
                    if(correctRequest) {
                        JSONResponse.put("resultat", "Réussite");
                    } else {
                        JSONResponse.put("resultat", "Echec");
                    }
                    //Log de debug.
                    if(debugMode) {
                        System.out.println("[Compagnie aérienne][Agent = " + getLocalName() + "] " +
                                           "Envoi d'une réponse à la requête de réservation: " + JSONResponse + ".");
                    }

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