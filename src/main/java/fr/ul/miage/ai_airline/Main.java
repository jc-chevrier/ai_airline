package fr.ul.miage.ai_airline;

import fr.ul.miage.ai_airline.build.Builder;
import fr.ul.miage.ai_airline.data_structure.City;
import fr.ul.miage.ai_airline.data_structure.Flight;
import fr.ul.miage.ai_airline.data_structure.Plane;
import fr.ul.miage.ai_airline.data_structure.PlaneType;
import fr.ul.miage.ai_airline.orm.ORM;

import java.util.Date;

/**
 * Classe principale du projet.
 */
public class Main {
    /**
     * Lancer l'application.
     */
    public static void main(String[] args) {
        //Construction de l'état de base de la
        //compagnie aérienne.
        Builder.build();
        //Lancement des agents de la compagnie
        //aérienne.

        //Exemple ORM.
        //Select *
        //ORM.getInstance().findAll(PlaneType.class).forEach(System.out::println);
        //var list = ORM.getInstance().findAll(PlaneType.class);
        //list.get(0);
        //Select *
        //ORM.getInstance().findAll(Plane.class).forEach(System.out::println);
        //Select where
        //ORM.getInstance().findWhere("WHERE sale_price < 800000", PlaneType.class).forEach(System.out::println)
        //Insert
        //Plane plane = new Plane();
        //plane.setPlaneTypeId(1);
        //ORM.getInstance().save(plane);
        //ORM.getInstance().findAll(Plane.class).forEach(System.out::println);
        //Update
        //var entity = (MyEntity) ORM.getInstance().findOne(...);
        //entity.set(...);
        //ORM.getInstance().save(entity);
    }
}