package fr.ul.miage.ai_airline;

import fr.ul.miage.ai_airline.dataset.Builder;
import fr.ul.miage.ai_airline.data_structure.Plane;
import fr.ul.miage.ai_airline.data_structure.PlaneType;
import fr.ul.miage.ai_airline.orm.ORM;

/**
 * Classe principale du projet.
 */
public class MainTest {
    /**
     * Lancer l'application.
     */
    public static void main(String[] args) {
        //Construction de l'état de base de la
        //compagnie aérienne.
        ORM.getInstance().removeAll(Plane.class);
        Builder.build();
        ORM.getInstance().findAll(Plane.class).forEach(entity -> {
            var plane = (Plane) entity;
            System.out.println(plane);
            System.out.println(ORM.getInstance().findOne(plane.getPlaneTypeId(), PlaneType.class));
        });
        System.out.println(ORM.getInstance().findNative("SELECT SUM(PT.sale_price) FROM PLANE P INNER JOIN PLANE_TYPE PT ON P.PLANE_TYPE_ID = PT.ID"));
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