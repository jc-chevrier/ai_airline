package fr.ul.miage.ai_airline.dataset;

import fr.ul.miage.ai_airline.configuration.Configuration;
import fr.ul.miage.ai_airline.data_structure.*;
import fr.ul.miage.ai_airline.orm.Entity;
import fr.ul.miage.ai_airline.orm.ORM;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;

/**
 * Générateur pour le peuplement de la
 * base de données.
 */
public class Builder {
    /**
     * Générer l'état de base de la compagnie
     * aérienne, la peupler.
     */
    public static void build() {
        //Récupération de l'ORM pour l'interrogation
        //de la base de données.
        var orm = ORM.getInstance();

        //Récupération de la configuration globale.
        var globalConfiguration = Configuration.GLOBAl_CONFIGURATION;
        var debugMode = Boolean.parseBoolean(globalConfiguration.getProperty("debug_mode"));

        //Rcéupération du contexte global.
        Global global = (Global) orm.findOne(1, Global.class);
        
        //Etape 1 : nettoyage des tables.
        //Log de debug.
        if(debugMode) {
            System.out.println("[Compagnie aérienne][Jeu de données] Nettoyage des vols et avions.");
        }
        //Nettoyage.
        orm.reset(FlightClass.class);
        orm.reset(Flight.class);
        orm.reset(Plane.class);

        //Etape 2 : génération des avions.
        //Log de debug.
        if(debugMode) {
            System.out.println("[Compagnie aérienne][Jeu de données] Génération des avions.");
        }
        //Génération.
        var poolMoneyInitialization = global.getBalanceInitialization();
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

            //Achat d'un avion.
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

        //Etape 3 : génération des vols.
        //Log de debug.
        if(debugMode) {
            System.out.println("[Compagnie aérienne][Jeu de données] Génération des vols.");
        }
        //Génération.
        var planes = orm.findAll(Plane.class);
        var startCityParis = orm.findOne(1, City.class);
        var citysWithoutParis = orm.findWhere("WHERE ID != 1", City.class);
        var startDate = Date.from(Instant.parse("2022-01-27T23:00:00.00Z"));//Décalage d'une heure car UTC + 1.//TODO à rediscuter.
        var endDate = Date.from(Instant.parse("2022-01-30T22:59:00.00Z"));//TODO à rediscuter.
        var fuelCostEuroPerL = 0.4;
        var consommationFuelCostLPerKm = 8.39;
        var taxePassengerEuro = 18;
        var maintenanceCost = 1070;
        var rateFirstClass = 0.5;
        var rateEconomicClass = 0.3;
        var rateBusinessClass = 0.2;
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
                for(var entity2 : planes) {
                    plane = (Plane) entity2;
                    //Vérification de la disponibilité.
                    planeAvailable = orm.findWhere("INNER JOIN CITY AS C " +
                                                    "ON C.ID = FROM_TABLE.ARRIVAL_CITY_ID " +
                                                    "WHERE FROM_TABLE.PLANE_ID = " + plane.getId() + " " +
                                                    "AND (EXTRACT(EPOCH FROM FROM_TABLE.ARRIVAL_DATE) + C.TIME_TO_PARIS) >= " +//TODO à rediscuter.
                                                    startDate.toInstant().getEpochSecond(), Flight.class).isEmpty();
                    //Si l'avion est disponible.
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

                //Récupération du type d'avion et de ses classes.
                var planeType = (PlaneType) orm.findOneWhere("WHERE ID = " + plane.getPlaneTypeId(), PlaneType.class);
                var planeTypeClasses = orm.findWhere("WHERE PLANE_TYPE_ID = " + planeType.getId(), PlaneTypeClass.class);

                //Calcul de la date d'arrivée.
                var arrivalDate = Date.from(startDate.toInstant().plusSeconds(arrivalCity.getTimeToParis()));

                //Création et insertion du vol.
                var flight = new Flight();
                flight.setStartDate(startDate);
                flight.setArrivalDate(arrivalDate);
                flight.setFloorPrice((arrivalCity.getDistanceToParis() * consommationFuelCostLPerKm * fuelCostEuroPerL) +
                                     (planeType.getCountTotalPlaces() * taxePassengerEuro));
                flight.setStartCityId(startCityParis.getId());
                flight.setArrivalCityId(arrivalCity.getId());
                flight.setPlaneId(plane.getId());
                orm.save(flight);

                //Création et insertion des classes du vol.
                Double floorPricePerPlace = flight.getFloorPrice() / planeType.getCountTotalPlaces();
                Boolean isCargo = planeTypeClasses.size() > 1;
                for(var entity2 : planeTypeClasses) {
                    var planeTypeClass = (PlaneTypeClass) entity2;
                    //Calcul du prix plancher de la place dans la classe.
                    Double floorPlacePrice;
                    if(isCargo) {
                        floorPlacePrice = floorPricePerPlace;
                        switch (planeTypeClass.getName()) {
                            case PlaneTypeClass.FIRST -> floorPricePerPlace *= rateFirstClass;
                            case PlaneTypeClass.BUSINESS -> floorPricePerPlace *= rateBusinessClass;
                            case PlaneTypeClass.ECONOMIC -> floorPricePerPlace *= rateEconomicClass;
                        }
                    } else {
                        floorPlacePrice = floorPricePerPlace;
                    }
                    //Création et insertion d'une classe du vol.
                    var flightClass = new FlightClass();
                    flightClass.setFloorPlacePrice(floorPlacePrice);
                    flightClass.setPlacePrice(flightClass.getFloorPlacePrice() + flightClass.getFloorPlacePrice() * 0.1);//TODO à rediscuter.
                    flightClass.setCountAvailablePlaces(planeTypeClass.getCountTotalPlaces());
                    flightClass.setCountOccupiedPlaces(0);
                    flightClass.setFlightId(flight.getId());
                    flightClass.setPlaneTypeClassId(planeTypeClass.getId());
                    orm.save(flightClass);
                }
            }
        }

        //Etape 4 : mise à jour du contexte global.
        //Log de debug.
        if(debugMode) {
            System.out.println("[Compagnie aérienne][Jeu de données] Mise à jour du contexte global.");
        }
        //Modification.
        var costInitialization = (Double) orm.findNative("SELECT SUM(PT.sale_price) AS COST_INITIALIZATION " +
                                                         "FROM PLANE P " +
                                                         "INNER JOIN PLANE_TYPE PT " +
                                                         "ON P.PLANE_TYPE_ID = PT.ID")
                                             .get(0)
                                             .get("COST_INITIALIZATION");
        global.incrementBalance(-costInitialization);
        orm.save(global);
    }
}