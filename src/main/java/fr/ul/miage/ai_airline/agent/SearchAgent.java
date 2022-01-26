package fr.ul.miage.ai_airline.agent;

import fr.ul.miage.ai_airline.configuration.Configuration;
import fr.ul.miage.ai_airline.data_structure.*;
import fr.ul.miage.ai_airline.orm.ORM;
import fr.ul.miage.ai_airline.tool.DateConverter;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Agent pour la gestion des requêtes
 * de recherche de vol.
 */
public class SearchAgent extends Agent {
    @Override
    protected void setup() {
        //Récupération de l'ORM pour l'interrogation
        //de la base de données.
        var orm = ORM.getInstance();

        //Récupération de la configuration globale.
        var globalConfiguration = Configuration.GLOBAl_CONFIGURATION;
        var debugMode = Boolean.parseBoolean(globalConfiguration.getProperty("debug_mode"));

        //Log de debug.
        if (debugMode) {
            System.out.println("[Compagnie aérienne] Initialisation d'un nouvel agent de recherche: " +
                               getLocalName() + " aka " + getAID().getName() + ".");
        }

        //Comportement d'écoute et de gestion des
        //requêtes de recherche.
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                //Log de debug.
                if (debugMode) {
                    System.out.println("[Compagnie aérienne][" + getLocalName() + "] " +
                                       "Nouvelle écoute des requêtes de recherche.");
                }

                //Attente d'une nouvelle requête de recherche.
                var messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
                var request = receive(messageTemplate);

                //Nouvelle requête de recherche.
                if (request != null) {
                    //Log de debug.
                    if (debugMode) {
                        System.out.println("[Compagnie aérienne][" + getLocalName() + "] " +
                                           "Nouvelle requête de recherche reçue de: " +
                                           request.getSender().getLocalName() + ".");
                    }

                    //Analyse de la requête et extraction de ses données.
                    JSONObject JSONRequest = null;
                    Integer requestId = null, seatsRequested = null;
                    Integer seatsAvalaible = null;
                    Date startDate = null;
                    Double upperPriceLimit = null, lowerPriceLimit = null;
                    String arrivalCityName = null, className = null;
                    try {
                        //Analyse de contenu de la requête.
                        JSONRequest = new JSONObject(request.getContent());
                        //Log de debug.
                        if (debugMode) {
                            System.out.println("[Compagnie aérienne][" + getLocalName() + "] " +
                                               "Contenu de la requête de recherche reçue: " + JSONRequest + "!");
                        }
                        //Extraction des données du contenu de la requête reçue.
                        startDate = DateConverter.stringToDate(JSONRequest.getString("dateDepart"));
                        requestId = JSONRequest.getInt("idRequete");
                        upperPriceLimit = JSONRequest.getDouble("prixHaut");
                        lowerPriceLimit = JSONRequest.getDouble("prixBas");
                        arrivalCityName = JSONRequest.getString("destination");
                        className = JSONRequest.getString("classe");
                        seatsRequested = JSONRequest.getInt("nbPlaces");

                    } catch (JSONException e) {
                        System.err.println("[Compagnie aérienne][" + getLocalName() + "] " +
                                           "Erreur! Problème à l'analyse d'une requête de recherche : " +
                                            request.getContent() + "!");
                        e.printStackTrace();
                        System.exit(1);
                    }

                    //Préparation de la réponse.
                    //Vérification des données de la requête et exécution de
                    //la requête si vérification réussie.
                    var correctRequest = true;
                    var JSONArrayFlights = new JSONArray();
                    //Si la classe demandée existe.
                    if (Arrays.asList(PlaneTypeClass.FIRST, PlaneTypeClass.BUSINESS, PlaneTypeClass.ECONOMIC)
                            .contains(className)) {
                        var arrivalCity = (City) orm.findOneWhere("WHERE NAME = '" + arrivalCityName + "'", City.class);
                        //Si la ville d'arrivée existe.
                        if (arrivalCity != null) {
                            var formatOnlyDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                                                   .withZone(ZoneId.of(Configuration.GLOBAl_CONFIGURATION.getProperty("timezone")));
                            //Récupération des vols correspondant aux filtres.
                            var flights = orm.findWhere("WHERE FROM_TABLE.ARRIVAL_CITY_ID = " +
                                                        arrivalCity.getId() + " " +
                                                        "AND EXTRACT(EPOCH FROM FROM_TABLE.START_DATE) >= " +
                                                        startDate.toInstant().getEpochSecond() + " " +
                                                        "AND TO_CHAR(FROM_TABLE.START_DATE, 'YYYY-MM-DD') = '" +
                                                        formatOnlyDate.format(startDate.toInstant()) + "' " +
                                                        "AND FROM_TABLE.ID IN " +
                                                            "(SELECT FC.FLIGHT_ID " +
                                                            "FROM FLIGHT_CLASS AS FC " +
                                                            "INNER JOIN PLANE_TYPE_CLASS AS PTC " +
                                                            "ON PTC.ID = FC.PLANE_TYPE_CLASS_ID " +
                                                            "WHERE PTC.NAME = '" + className + "' " +
                                                            "AND FC.COUNT_AVAILABLE_PLACES > 0 " +
                                                            "AND FC.PLACE_PRICE >= " + lowerPriceLimit + " " +
                                                            "AND FC.PLACE_PRICE <= " + upperPriceLimit + ") " +
                                                            "ORDER BY FROM_TABLE.START_DATE",
                                                        Flight.class);
                            //Log de debug.
                            if (debugMode) {
                                System.out.println("[Compagnie aérienne][" + getLocalName() + "] " +
                                                   "Vols filtrés trouvés : " +
                                                    new JSONArray(flights) + ".");
                            }
                            //Création d'une vue des vols trouvés.
                            for (var entity : flights) {
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
                                JSONFlight.put("recommandationScore", 0);
                                JSONArray JSONArrayFlightClasses = new JSONArray();
                                JSONFlight.put("classes", JSONArrayFlightClasses);
                                //Récupération des classes du vol.
                                var flightClasses = orm.findWhere("INNER JOIN PLANE_TYPE_CLASS AS PTC " +
                                                                  "ON PTC.ID = FROM_TABLE.PLANE_TYPE_CLASS_ID " +
                                                                  "WHERE FROM_TABLE.FLIGHT_ID = " + flight.getId() + " " +
                                                                  "AND PTC.NAME = '" + className + "'",
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
                                    //Mémorisation du nombre de places disponibles.
                                    seatsAvalaible = flightClass.getCountAvailablePlaces()-flightClass.getCountOccupiedPlaces();
                                }
                                if (seatsRequested <= seatsAvalaible){
                                    JSONArrayFlights.put(JSONFlight);
                                }

                            }
                        } else {
                            correctRequest = false;
                        }
                    } else {
                        correctRequest = false;
                    }

                    //Si des vols ont été trouvés.
                    if(!JSONArrayFlights.isEmpty()) {
                        //Calcul des scores de recommandation.
                        setScore(JSONArrayFlights, startDate, seatsRequested);
                        //Tri des résultats en fonction des scores de recommandation.
                        JSONArrayFlights = sortByScore(JSONArrayFlights);
                    }

                    //Création de la réponse.
                    var JSONResponse = new JSONObject();
                    JSONResponse.put("idRequete", requestId);
                    //Si la requête était correcte.
                    if (correctRequest) {
                        JSONResponse.put("vols", JSONArrayFlights);
                    } else {
                        JSONResponse.put("resultat", "Echec");
                    }

                    //Log de debug.
                    if (debugMode) {
                        System.out.println("[Compagnie aérienne][" + getLocalName() + "] " +
                                           "Envoi d'une réponse à la requête de recherche: " + JSONResponse + ".");
                    }

                    //Envoi de la réponse.
                    var response = request.createReply();
                    //Si la requête était correcte.
                    if (correctRequest) {
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

    /**
     * Affecter un score de recommendation selon plusieurs critères.
     *
     * @param jsonFlights
     * @param requestedDate
     * @param countPlacesAsked
     */
    private void setScore(JSONArray jsonFlights, Date requestedDate, Integer countPlacesAsked) {
        //Récupération de l'ORM pour l'interrogation
        //de la base de données.
        var orm = ORM.getInstance();

        //Récupération du contexte global.
        Global global = (Global) orm.findOne(1, Global.class);

        //On initialise la valeur de comparaison avec le premier résultat.
        Double lowestPriceFound = jsonFlights.getJSONObject(0).getJSONArray("classes").getJSONObject(0).getDouble("prixPlace");

        //On commence par chercher le vol le moins cher parmi tous les résultats.
        for (Object jsonFlight : jsonFlights) {
            Double currentPrice = ((JSONObject) jsonFlight).getJSONArray("classes").getJSONObject(0).getDouble("prixPlace");
            if (lowestPriceFound > currentPrice) {
                lowestPriceFound = currentPrice;
            }
        }

        //On attribue les scores selon différents critères.
        for (Object jsonFlight : jsonFlights) {
            //On connait le prix le plus petit, on cherche le vol avec ce prix et on lui donne 1.
            Double currentPrice = ((JSONObject) jsonFlight).getJSONArray("classes").getJSONObject(0).getDouble("prixPlace");
            if (lowestPriceFound == currentPrice) {
                int currentRecommandationScore = ((JSONObject) jsonFlight).getInt("recommandationScore");
                currentRecommandationScore++;
                ((JSONObject) jsonFlight).put("recommandationScore", currentRecommandationScore);
            }

            //Si l'avion a encore plus de 10 places disponibles dans la classe demandée alors on lui ajoute 1.
            int avalaibleSeats = ((JSONObject) jsonFlight).getJSONArray("classes").getJSONObject(0).getInt("nbPlaces");
            if (avalaibleSeats >= (countPlacesAsked + global.getOffsetPLacesRecommandation())) {
                int currentRecommandationScore = ((JSONObject) jsonFlight).getInt("recommandationScore");
                currentRecommandationScore++;
                ((JSONObject) jsonFlight).put("recommandationScore", currentRecommandationScore);
            }

            //Si l'heure du décollage est à moins de 2h de celle demandée.
            String flightDateTakeoffStr = ((JSONObject) jsonFlight).getString("dateDepart");
            Date flightDateTakeoff = DateConverter.stringToDate(flightDateTakeoffStr);
            int timeToWait = flightDateTakeoff.getHours() - requestedDate.getHours();
            if (timeToWait <= global.getOffsetHoursRecommandation()) {
                int currentRecommandationScore = ((JSONObject) jsonFlight).getInt("recommandationScore");
                currentRecommandationScore++;
                ((JSONObject) jsonFlight).put("recommandationScore", currentRecommandationScore);
            }
        }
    }

    /**
     * Trier des vols retournés par score de recommendation décroissant.
     *
     * @param jsonFlights
     * @return
     */
    private JSONArray sortByScore(JSONArray jsonFlights) {
        //On trie dans l'ordre décroissant via le champ "recommandationScore".
        List<Object> list = jsonFlights.toList();
        list.sort(Comparator.comparingInt(o -> ((JSONObject)o).getInt("recommandationScore")).reversed());
        return new JSONArray(list);
    }
}