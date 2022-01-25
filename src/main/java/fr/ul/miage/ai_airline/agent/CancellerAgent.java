package fr.ul.miage.ai_airline.agent;

import fr.ul.miage.ai_airline.configuration.Configuration;
import fr.ul.miage.ai_airline.data_structure.Flight;
import fr.ul.miage.ai_airline.data_structure.FlightClass;
import fr.ul.miage.ai_airline.data_structure.Global;
import fr.ul.miage.ai_airline.orm.ORM;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

/**
 * Agent pour l'annulation de vols vides à venir'
 *
 */
public class CancellerAgent extends Agent {
    @Override
    protected void setup() {
        //Récupération de l'ORM pour l'interrogation de la base de données.
        var orm = ORM.getInstance();

        //Récupération de la configuration globale.
        var globalConfiguration = Configuration.GLOBAl_CONFIGURATION;
        var debugMode = Boolean.parseBoolean(globalConfiguration.getProperty("debug_mode"));

        //Récupération du contexte global.
        Global global = (Global) orm.findOne(1, Global.class);

        //Log de debug.
        if(debugMode) {
            System.out.println("[Compagnie aérienne] Initialisation d'un nouvel agent d'annulation de vols vides : " +
                    getLocalName() + " aka " + getAID().getName() + ".");
        }

        // Comportement d'écoute et de gestion des requêtes de réservation.
        addBehaviour(new TickerBehaviour(this, 10000) {

            @Override
            protected void onTick() {
                //Log de debug.
                if(debugMode) {
                    System.err.println("[Compagnie aérienne][Agent = " + getLocalName() + "] " +
                            "Nettoyage des vols vides à venir.");
                }

                // Trouver les vols vides des 2 prochains jours
                var flights = orm.findWhere("WHERE start_date < NOW() + interval '7' day",Flight.class);
                for (var flight : flights) {

                    //Récupération des classes du vol.
                    var flightClasses = orm.findWhere("WHERE flight_id = " + flight.getId(),
                            FlightClass.class);
                    double sumCapacity = 0;
                    double sumOccupiedSeats = 0;

                    for (var entity : flightClasses) {
                        var flightClass = (FlightClass) entity;
                        // On veut le pourcentage de sièges occupées à travers toutes les classes proposées (1ère,éco,etc)
                        sumCapacity += flightClass.getCountAvailablePlaces();
                        sumOccupiedSeats += flightClass.getCountOccupiedPlaces();
                    }

                    // Si 10% ou moins des places sont occupées alors le vol est annulé
                    double percent = (sumOccupiedSeats/sumCapacity)*100;
                    if (percent < 10.00){
                        // On supprime chaque classe du vol
                        for (var entity : flightClasses) {
                            var flightClass = (FlightClass) entity;
                            System.out.println("Suppression pour le vol "+ flight.getId() + " de sa classe (id) " + flightClass.getPlaneTypeClassId());
                            orm.removeOne(flightClass);
                        }
                        // Enfin, on supprime le vol
                        System.out.println("Suppression pour du vol "+ flight.getId());
                        orm.removeOne(flight);
                    }

                    }
            }
        });
    }
}
