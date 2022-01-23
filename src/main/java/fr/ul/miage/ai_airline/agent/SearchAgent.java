package fr.ul.miage.ai_airline.agent;

import fr.ul.miage.ai_airline.configuration.Configuration;
import fr.ul.miage.ai_airline.data_structure.*;
import fr.ul.miage.ai_airline.orm.Entity;
import fr.ul.miage.ai_airline.orm.ORM;
import fr.ul.miage.ai_airline.tool.DateConverter;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Agent pour la gestion des requêtes
 * de recherche de vol.
 */
public class SearchAgent extends Agent {
    //ORM pour l'échange avec la base de données.
    private static ORM orm = ORM.getInstance();

    @Override
    protected void setup() {
        //Récupération de la configuration globale.
        var globalConfiguration = Configuration.GLOBAl_CONFIGURATION;
        var debugMode = Boolean.parseBoolean(globalConfiguration.getProperty("debugMode"));

        //Log de debug.
        if(debugMode) {
            System.out.println("[Domaine = compagnie aérienne] Initialisation d'un nouvel agent de recherche: " +
                                 getLocalName() + " aka " + getAID().getName() + ".");
        }

        //Définition du comportement.
        addBehaviour(new TickerBehaviour(this, 1000) {
            @Override
            protected void onTick() {
                //Log de debug.
                if(debugMode) {
                    System.out.println("[Domaine = compagnie aérienne][Agent = " + getLocalName() + "] " +
                                       "Nouvelle écoute des requêtes de recherche.");
                }

                //Attente d'une nouvelle requête de recherche.
                var messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
                var request = receive(messageTemplate);

                //Nouvelle requête de recherche.
                if (request != null) {
                    //Log de debug.
                    if(debugMode) {
                        System.out.println("[Domaine = compagnie aérienne][Agent = " + getLocalName() + "] " +
                                           "Nouvelle requête de recherche reçue de: " +
                                           request.getSender().getLocalName() + ".");
                    }

                    //Analyse de la requête et extraction de ses données.
                    JSONObject JSONRequest = null;
                    Integer requestId = null;
                    Date startDate = null;
                    Double upperPriceLimit = null, lowerPriceLimit = null;
                    String arrivalCityName = null, className = null;
                    try {
                        //Analyse de contenu de la requête.
                        JSONRequest = new JSONObject(request.getContent());
                        //Log de debug.
                        if(debugMode) {
                            System.out.println("[Domaine = compagnie aérienne][Agent = " + getLocalName() + "] " +
                                               "Contenu de la requête de recherche reçue: " + JSONRequest + "!");
                        }
                        //Extraction des données du contenu de la requête reçue.
                        startDate = DateConverter.stringToDate(JSONRequest.getString("dateDepart"));
                        requestId = JSONRequest.getInt("idRequete");
                        upperPriceLimit = JSONRequest.getDouble("prixHaut");
                        lowerPriceLimit = JSONRequest.getDouble("prixBas");
                        arrivalCityName = JSONRequest.getString("destination");
                        className = JSONRequest.getString("classe");
                    } catch (JSONException e) {
                        System.err.println("[Domaine = compagnie aérienne][Agent = " + getLocalName() + "] " +
                                           "Erreur! Problème à l'analyse d'une requête de recherche : " + request.getContent() + "!");
                        e.printStackTrace();
                        System.exit(1);
                    }

                    //Préparation de la réponse.
                    //Vérification des données de la requête et exécution de
                    //la requête si vérification réussie.
                    var correctRequest = true;
                    var JSONArrayFlights = new JSONArray();
                    //Si la classe demandée existe.
                    if(Arrays.asList(PlaneTypeClass.FIRST, PlaneTypeClass.BUSINESS, PlaneTypeClass.ECONOMIC)
                             .contains(className)) {
                        var arrivalCity = (City) orm.findOneWhere("WHERE NAME = '" + arrivalCityName + "'", City.class);
                        //Si la ville d'arrivée existe.
                        if(arrivalCity != null) {
                            //Récupération des vols correspondant aux filtres.
                            var flights = orm.findWhere("INNER JOIN FLIGHT_CLASS AS FC " +
                                                        "ON FC.FLIGHT_ID = FROM_TABLE.ID " +
                                                        "WHERE FROM_TABLE.ARRIVAL_CITY_ID = " +
                                                        arrivalCity.getId() + " " +
                                                        "AND FC.PLACE_PRICE >= " + lowerPriceLimit + " " +
                                                        "AND FC.PLACE_PRICE <= " + upperPriceLimit + " " +
                                                        "AND EXTRACT(EPOCH FROM FROM_TABLE.START_DATE) >= " +
                                                        startDate.toInstant().getEpochSecond() + " " +
                                                        "AND FORM_TABLE.ID IN " +
                                                            "(SELECT FC2.FLIGHT_ID " +
                                                            "FROM FLIGHT_CLASS FC2 " +
                                                            "INNER JOIN PLANE_TYPE_CLASS AS PTC " +
                                                            "ON PTC.ID = FC2.PLANE_TYPE_CLASS_ID " +
                                                            "WHERE PTC.NAME = '" + className + "' " +
                                                            "AND FC2.COUNT_AVAILABLE_PLACES > 0)",
                                    Flight.class);
                            //Log de debug.
                            if(debugMode) {
                                System.out.println("[Domaine = compagnie aérienne][Agent = " + getLocalName() + "] " +
                                                   "Vols filtrés trouvés : " + new JSONObject(flights) + ".");
                            }
                            //Création d'une vue des vols trouvés.
                            for(var entity : flights) {
                                var flight = (Flight) entity;
                                //Récupération des entités liées à un vol.
                                var plane = (Plane) orm.findOneWhere("WHERE ID = " + flight.getPlaneId(), Plane.class);
                                var planeType = (PlaneType) orm.findOneWhere("WHERE ID = " + plane.getPlaneTypeId(), PlaneType.class);
                                var startCity = (City) orm.findOneWhere("WHERE ID = " + flight.getStartCityId(), City.class);
                                //Création d'une vue d'un vol trouvé.
                                JSONObject JSONFlight = new JSONObject();
                                JSONFlight.put("id", flight.getId());
                                JSONFlight.put("villeDepart", startCity.getName());
                                JSONFlight.put("villeArrivee", arrivalCity.getName());
                                JSONFlight.put("dateDepart", DateConverter.dateToString(flight.getStartDate()));
                                JSONFlight.put("dateArrivee", DateConverter.dateToString(flight.getArrivalDate()));
                                JSONFlight.put("typeAvion", planeType.getName());
                                JSONArray JSONArrayFlightClasses = new JSONArray();
                                JSONFlight.put("classes", JSONArrayFlightClasses);
                                //Récupération des classes du vol.
                                var flightClasses = orm.findWhere("INNER JOIN PLANE_TYPE_CLASS AS PTC " +
                                                                  "ON PTC.ID = FROM_TABLE.PLANE_TYPE_CLASS_ID " +
                                                                  "WHERE FROM_TABLE.FLIGHT_ID = " + flight.getId() + " " +
                                                                  "AND PTC.NAME = '" + className + "')",
                                        FlightClass.class);
                                //Création des vues des classes du voltrouvées.
                                for (var entity2 : flightClasses) {
                                    var flightClass = (FlightClass) entity2;
                                    //Création d'une vue d'une classe du vol trouvée.
                                    JSONObject JSONFlightClass = new JSONObject();
                                    JSONFlightClass.put("type", className);
                                    JSONFlightClass.put("nbPlaces", flightClass.getCountAvailablePlaces());
                                    JSONFlightClass.put("prixPlace", flightClass.getPlacePrice());
                                    JSONArrayFlightClasses.put(JSONFlightClass);
                                }
                                JSONArrayFlights.put(JSONFlight);
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
                        JSONResponse.put("vols", JSONArrayFlights);
                    } else {
                        JSONResponse.put("resultat", "Echec");
                    }
                    //Log de debug.
                    if(debugMode) {
                        System.out.println("[Domaine = compagnie aérienne][Agent = " + getLocalName() + "] " +
                                            "Envoi d'une réponse à la requête de recherche: " + JSONResponse + ".");
                    }

                    //Envoi de la réponse.
                    var response = request.createReply();
                    //Si la requête était correcte.
                    if(correctRequest) {
                        response.setPerformative(ACLMessage.INFORM);
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


