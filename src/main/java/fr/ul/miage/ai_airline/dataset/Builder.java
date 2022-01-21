package fr.ul.miage.ai_airline.dataset;

import fr.ul.miage.ai_airline.data_structure.*;
import fr.ul.miage.ai_airline.orm.Entity;
import fr.ul.miage.ai_airline.orm.ORM;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;

/**
 * Constructeur pour le peuplement
 * de la base de données.
 */
public class Builder {
    private static ORM orm = ORM.getInstance();

    /**
     * Construire l'état de base de
     * la compagnie aérienne, la peupler.
     */
    public static void build() {
        //Etape 1 : nettoyage des tables.
        orm.removeAll(FlightClass.class);
        orm.removeAll(Flight.class);
        orm.removeAll(Plane.class);

        //Etape 2 : construction des avions.
        var poolMoneyInitialization = 10000000;
        var continueToCreateAvion = true;
        while(continueToCreateAvion) {
            //Choix aléatoire d'un identifiant de type d'avion.//TODO à rediscuter.
            Integer randomPlaneTypeId;
            //Tant que l'identifiant vaut 0.
            do {
              //Tirage.
              randomPlaneTypeId = ((Long) Math.round(Math.random() * 6)).intValue();
            } while (randomPlaneTypeId == 0);
            //Récupération du type d'avion.
            var planeType = (PlaneType) orm.findOne(randomPlaneTypeId, PlaneType.class);

            //Si assez d'argent pour acheter l'avion.
            if(planeType.getSalePrice() <= poolMoneyInitialization) {
                //Création et insertion d'un nouvel avion.
                var plane = new Plane();
                plane.setPlaneTypeId(planeType.getId());
                orm.save(plane);

                //Modification du solde d'argent restant.
                poolMoneyInitialization -= planeType.getSalePrice();
            } else {
                //Fin de la création des avions.
                continueToCreateAvion = false;
            }
        }

        //Etape 3 : construction des vols.
        var planes = orm.findAll(Plane.class);
        var startCityParis = orm.findOne(1, City.class);
        var citysWithoutParis = orm.findWhere("WHERE ID != 1", City.class);
        var startDate = Date.from(Instant.parse("2022-01-27T23:00:00.00Z"));//Décalage d'une heure car UTC + 1.//TODO à rediscuter.
        var endDate = Date.from(Instant.parse("2022-01-30T22:59:00.00Z"));//TODO à rediscuter.
        //Tant que date de fin de période des vols pas atteinte.
        while(startDate.before(endDate)) {
            //Choix de la ville d'arrivée.
            //Mélange des villes pour ne pas en favoriser.
            Collections.shuffle(citysWithoutParis);
            //Pour plus de viles possibles, on essaie de proposer des créneaux.
            for(Entity entity : citysWithoutParis) {
                var arrivalCity = (City) entity;

                //Choix de l'avion.
                //Mélange des avions pour ne pas en favoriser.
                Collections.shuffle(planes);
                Plane plane = null;
                Boolean planeAvailable = false;
                //Tant que pas d'avion disponible trouvé.
                for(Entity entity2 : planes) {
                    plane = (Plane) entity2;
                    planeAvailable = orm.findWhere("INNER JOIN CITY AS C " +
                                                    "ON C.ID = FROM_TABLE.ARRIVAL_CITY_ID " +
                                                    "WHERE FROM_TABLE.PLANE_ID = " + plane.getId() + " " +
                                                    "AND (EXTRACT(EPOCH FROM FROM_TABLE.ARRIVAL_DATE) + C.TIME_TO_PARIS) >= " +
                                                    startDate.toInstant().getEpochSecond(), Flight.class).isEmpty();
                    if(planeAvailable) {
                        break;
                    }
                }
                //Si aucun avion disponible trouvé.
                if(!planeAvailable) {
                    //Abandon du créneau, et avance de 10 minutes pour trouver le créneau suivant.
                    startDate = Date.from(startDate.toInstant().plusSeconds(10 * 60));
                    break;
                }

                //Calcul de la date d'arrivée.
                var arrivalDate = Date.from(startDate.toInstant().plusSeconds(arrivalCity.getTimeToParis()));

                //Création et insertion du vol.
                var flight = new Flight();
                flight.setStartDate(startDate);
                flight.setArrivalDate(arrivalDate);
                flight.setFloorPrice(0.0);
                flight.setStartCityId(startCityParis.getId());
                flight.setArrivalCityId(arrivalCity.getId());
                flight.setPlaneId(plane.getId());
                orm.save(flight);

                //Création et insertion des classes du vol.
                var planeType = orm.findOneWhere("WHERE ID = " + plane.getPlaneTypeId(), PlaneType.class);
                var planeTypeClasses = orm.findWhere("WHERE PLANE_TYPE_ID = " + planeType.getId(), PlaneTypeClass.class);
                for(Entity entity2 : planeTypeClasses) {
                    //Création et insertion d'une classe du vol.
                    var planeTypeClass = (PlaneTypeClass) entity2;
                    var flightClass = new FlightClass();
                    flightClass.setFloorPlacePrice(0.0);
                    flightClass.setPlacePrice(0.0);
                    flightClass.setCountAvailablePlaces(planeTypeClass.getCountTotalPlaces());
                    flightClass.setCountOccupiedPlaces(0);
                    flightClass.setFlightId(flight.getId());
                    flightClass.setPlaneTypeClassId(planeTypeClass.getId());
                    orm.save(flightClass);
                }
            }
        }

        //Sauvegarde des données globales compagnie.
        //TODO
    }
}
