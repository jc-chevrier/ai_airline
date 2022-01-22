package fr.ul.miage.ai_airline.agent;


        import fr.ul.miage.ai_airline.data_structure.*;
        import fr.ul.miage.ai_airline.orm.Entity;
        import fr.ul.miage.ai_airline.orm.ORM;
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
        import java.util.Date;
        import java.util.List;

public class ConsultationAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println("Je suis l'agent de la compagnie aérienne qui écoute les requêtes de consultation : " +
                getLocalName() + " appelé aussi " + getAID().getName());


        addBehaviour(new TickerBehaviour(this, 1000) {
            @Override
            protected void onTick() {
                System.out.println("Tick listening consultation");

                // Wait for incoming message of fipa-type ACLMessage.REQUEST
                MessageTemplate onlyREQUEST = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
                ACLMessage msg = receive(onlyREQUEST);
                if (msg != null) {
                    ACLMessage reply = msg.createReply();
                    System.out.println("Listention consultation agent === Message received from " + msg.getSender().getLocalName());
//                  System.out.println("Internal agent === Message content is : " + getLocalName() + " <- " + msg.getContent());
                    try {
                        System.out.println("----------- BEGIN ----------------");
                        System.out.println(msg.getContent());
                        System.out.println("----------- END ----------------");
                        JSONObject json = new JSONObject(msg.getContent());
                        String takeoffDate = json.getString("dateDepart");
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
                        // string : dateTime.format(formatter)  == 2022-01-28 11:30
                        // datetime == 2022-01-28T11:30
                        LocalDateTime takeoffTime = LocalDateTime.parse(takeoffDate, formatter);
                        String idResquest = json.getString("idRequete");
                        Double upperLimit = json.getDouble("prixHaut");
                        Double lowerLimit = json.getDouble("prixBas");
                        String destination = json.getString("destination");
                        String classe = json.getString("classe");


                        List<Entity> cities = ORM.getInstance().findWhere("WHERE name = '"+ destination +"'", City.class);
                        // Si ville non trouvée
                        if(cities.size() == 0){
                            reply.setPerformative(ACLMessage.UNKNOWN);
                            reply.setContent("You provided a unknown city name : " + destination);
                            send(reply);
                        }
                        int cityId = cities.get(0).getId();
                        System.out.println("Vols pour "+destination);
                        System.out.println("Vols pour "+cityId);
                        List<Entity> flights = ORM.getInstance().findWhere("WHERE arrival_city_id=" + cityId, Flight.class);
                        ORM.getInstance().findWhere("WHERE arrival_city_id=" + cityId, Flight.class).forEach(System.out::println);
                        System.out.println("Nombre de vols trouvés : "+flights.size());


                        // vols le 28 pour barcelone :  5 10 15 19 24
                        // vols le 30 pour barcelone : 53 60
                        JSONObject flightsAvaliable = new JSONObject();
                        flightsAvaliable.put("idRequete",idResquest);
                        flightsAvaliable.put("vols",new JSONArray());
                        JSONArray flightArray = flightsAvaliable.getJSONArray("vols");
                        ORM.getInstance().findWhere("WHERE arrival_city_id=" + cityId, Flight.class)
                                .forEach(
                                        entity -> {
                                            var flight = (Flight) entity;
                                            int flightId = flight.getId();
                                            Date takingoffDate = flight.getStartDate();
                                            String takingoff = formatter.format(takingoffDate.toInstant());
                                            Date landingDate = flight.getStartDate();
                                            String landing = formatter.format(landingDate.toInstant());
                                            int planeId = flight.getPlaneId();
                                            var plane = (Plane) ORM.getInstance().findOneWhere("WHERE id=" + planeId, Plane.class);
                                            var planeType = (PlaneType) ORM.getInstance().findOneWhere("WHERE id=" + plane.getPlaneTypeId(), PlaneType.class);
                                            String planeTypeName = planeType.getName();
                                            var cityFrom = (City) ORM.getInstance().findOneWhere("WHERE id=" + flight.getStartCityId(), City.class);
                                            String cityFromString = cityFrom.getName();
                                            var cityTo = (City) ORM.getInstance().findOneWhere("WHERE id=" + flight.getArrivalCityId(), City.class);
                                            String cityFromTo = cityTo.getName();

                                            JSONObject currentFlight = new JSONObject();
                                            currentFlight.put("id",flightId);
                                            currentFlight.put("villeDepart",cityFromString);
                                            currentFlight.put("villeArrivee",cityFromTo);
                                            currentFlight.put("dateDepart",takingoff);
                                            currentFlight.put("dateArrivee",landing);
                                            currentFlight.put("typeAvion",planeTypeName);
                                            currentFlight.put("classes",new JSONArray());
                                            JSONArray classesArray = currentFlight.getJSONArray("classes");


                                            ORM.getInstance().findWhere("WHERE flight_id=" + flightId,  FlightClass.class)
                                                    .forEach(
                                                            entity2 -> {
                                                                var flightClass = (FlightClass) entity2;
                                                                int avalaiblePlaces = flightClass.getCountAvailablePlaces();
                                                                Double pricePlace = flightClass.getPlacePrice();
                                                                var planeTypeClass = (PlaneTypeClass) ORM.getInstance().findOneWhere("WHERE id=" + flightClass.getPlaneTypeClassId(), PlaneTypeClass.class);
                                                                String className = planeTypeClass.getName();
                                                                JSONObject currentClass = new JSONObject();
                                                                currentClass.put("type",className);
                                                                currentClass.put("nbPlaces",avalaiblePlaces);
                                                                currentClass.put("prixPlace",pricePlace);
                                                                classesArray.put(currentClass);
                                                            }

                                                    );
                                            flightArray.put(currentFlight);

                                        }
                                        );


                        System.out.println("###########################################################");
                        System.out.println(flightsAvaliable.toString());


//                        {
//                            “idRequete”: identifiant de la requête,
//                              “vols”:
//                                [
//                                        {
//                                        “id”: identifiant_du_vol,
//                                        “villeDepart”: ville_de_départ,
//                                        “villeArrivee”: ville_arrivée,
//                                        “dateDepart”: date_et_heuredépart (string),
//                                        “dateArrivee”: date et heure d'arrivée estimé (string),
//                                        “typeAvion”: type d'avion (exemple : Cargo 1),
//                                        “classes” : [
//                                                    {
//                                                         “type”: type de la classe
//                                                         “nbPlaces”: nombre de places disponibles,
//                                                         “prixPlace”: prix de la place,
//                                                    }
//                                                    ]
//                                        }
//                                ]
//                        }




                    } catch (JSONException e) {
                        System.err.println("AGENT LISTEN CONSULTATION CRASH");
                        System.err.println(msg.getContent());
                        e.printStackTrace();
                        System.exit(1);
                    }

                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent("Pong");
                    send(reply);
                }
                block();

            }
        });

    }
}


