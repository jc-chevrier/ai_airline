package fr.ul.miage.ai_airline.dataset;

import fr.ul.miage.ai_airline.data_structure.*;
import fr.ul.miage.ai_airline.orm.Entity;
import fr.ul.miage.ai_airline.orm.ORM;

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
        //Etape 1 : construction des avions. TODO à revoir pour créer une stratégie.
        //Nettoyage de la table.
        orm.removeAll(Plane.class);
        //Peuplement de la table.
        var poolMoneyInitialization = 10000000;
        var continueToCreateAvion = true;
        while(continueToCreateAvion) {
            //Choix aléatoire d'un identifiant de type d'avion.
            Integer randomPlaneTypeId;
            //Tant que l'identifiant vaut 0.
            do {
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

        //Etape 2 : construction des vols.
        //Nettoyage des tables.
        orm.removeAll(FlightClass.class);
        orm.removeAll(Flight.class);
        //Peuplement des tables.
        var planes = orm.findAll(Plane.class);
        var cityParis = orm.findOne(1, City.class);
        var citysWithoutParis = orm.findWhere("WHERE ID != 1", City.class);
        while() {
            //Création et insertion du vol.
            var flight = new Flight();
            flight.setStartDate();
            flight.setArrivalDate();
            flight.setFloorPrice();
            flight.setStartCityId(cityParis.getId());
            flight.setArrivalCityId();
            flight.setPlaneId();

            //Création et insertion des classes du vol.
            var plane = new Plane();
            var planeType = orm.findOneWhere("WHERE ID = " + plane.getPlaneTypeId(), PlaneType.class);
            var planeTypeClasses = orm.findWhere("WHERE PLANE_TYPE_ID = " + planeType.getId(), PlaneTypeClass.class);
            for(Entity entity : planeTypeClasses) {
                //Création et insertion d'une classe du vol.
                var planeTypeClass = (PlaneTypeClass) entity;
                var flightClass = new FlightClass();
                flightClass.setFloorPlacePrice();
                flightClass.setPlacePrice();
                flightClass.setCountAvailablePlaces(planeTypeClass.getCountTotalPlaces());
                flightClass.setCountOccupiedPlaces(0);
                flightClass.setFlightId(flight.getId());
                flightClass.setPlaneTypeClassId(planeTypeClass.getId());
                orm.save(flightClass);
            }
        }
    }
}
