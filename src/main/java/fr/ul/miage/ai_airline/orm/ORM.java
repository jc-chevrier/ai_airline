package fr.ul.miage.ai_airline.orm;

import fr.ul.miage.ai_airline.Main;
import fr.ul.miage.ai_airline.configuration.Configuration;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * ORM, pour la sélection, et la manipulation de données
 * de base de données PostgreSQL.
 *
 * Sélection <=> recherche / récupération de données.
 *
 * Manipulation <=> insertion, mise à jour et suppression
 * de données.
 *
 * La classe citée ci-dessous est la classe parent
 * de toutes les classes entités du projet, représentant
 * les tables de la base de données  :
 * @see Entity
 * Elle est beaucoup utilisée par l'ORM.
 *
 * La classe citée ci-dessous est la classe outil nous
 * permettant d'extraire les métadonnées des entités :
 * @see EntityMetadata
 */
public class ORM {
    //Connexion à la base de données.
    private Connection connection;
    //Singleton.
    private static ORM singletonORM;

    private ORM() {
        connect();
    }

    /**
     * Obtenir le singleton ORM.
     *
     * @return
     */
    public static ORM getInstance() {
        if(singletonORM == null) {
            singletonORM = new ORM();
        }
        return singletonORM;
    }

    /**
     * Connecter l'application à la base de données.
     */
    private void connect() {
        var configuration = Configuration.DATABASE_CONFIGURATION;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://" +
                                                    configuration.get("host") + ":" + configuration.get("port") + "/" +
                                                    configuration.get("database"),
                                                    (String) configuration.get("user"), (String) configuration.get("password"));
            connection.setAutoCommit(false);
        } catch (Exception e) {
            System.err.println("Erreur ! Connexion impossible à la base de données !");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Chercher des n-uplets de manière "sauvage",
     * sans préciser d'entité correspondante.
     *
     * Cette méthode permet de faire une requête de recherche
     * entièrement écrite de l'extérieur, et passée ensuite
     * en paramètre.
     *
     * Cette méthode permet de faire des agrégations par
     * exemple, elle permet de tout faire sauf des
     * manipulations de données.
     *
     * L'ennui avec cette méthode est que vu qu'elle permet
     * de chercher "tout ce qu'on veut", son résultat ne peut pas
     * être retourné dans une entité / dans une classe bien
     * définie. Ainsi, son résultat est une liste de tables
     * associatives, chaque table associative de la liste
     * représentatnt un n-uplet.
     *
     * Si aucun n-uplet n'est trouvé, la méthode retourne une
     * liste vide.
     *
     * @param requestString
     * @return
     */
    public List<Map<String, Object>> findNative(@NotNull String requestString) {
        List<Map<String, Object>> entitys = new ArrayList<Map<String, Object>>();

        try {
            //Exécution de la requête.
            Statement request = connection.createStatement();
            ResultSet resultEntitys = request.executeQuery(requestString);

            //Récupération des libellés des attributes du résultat.
            ResultSetMetaData resultMetadatas = resultEntitys.getMetaData();
            List<String> attributes = new ArrayList<String>();
            int countAttributes = resultMetadatas.getColumnCount();
            for(int indexAttribute = 1; indexAttribute <= countAttributes; indexAttribute++) {
                attributes.add(resultMetadatas.getColumnName(indexAttribute).toUpperCase());
            }

            //Lecture du résultat de la requête.
            while(resultEntitys.next()) {
                Map<String, Object> entity = new HashMap<String, Object>();
                entitys.add(entity);
                for(String attribute : attributes) {
                    Object value = resultEntitys.getObject(attribute);
                    entity.put(attribute, value);
                }
            }

            //Fin de la lecture du résultat de la requête.
            resultEntitys.close();
            //Fin de la requête.
            request.close();
        } catch (Exception e) {
            System.err.println("Erreur ! Une requête se sélection a échouée : \"" + requestString + "\" !");
            e.printStackTrace();
            System.exit(1);
        }

        return entitys;
    }

    /**
     * Chercher des n-uplets d'une table
     * avec un prédicat.
     *
     * La requête en interne fait un "SELECT * FROM"
     * sur la table désignée par la classe entité
     * précisée en paramètre.
     *
     * Cette méthode de recherche / de sélection permet
     * de préciser un prédicat / une condition de recherche :
     * condition WHERE, et INNER JOIN avant possible si besoin
     * de faire des jointures pour le prédicat.
     *
     * Les n-uplets trouvés sont ordonnées avec le champ "ID".
     * On part du postulat que toute tables de nos bases
     * de données ont toujours une clé primaire
     * nommée "ID".
     *
     * Si aucun n-uplet n'est trouvé, la méthode retourne une
     * liste vide.
     *
     * @param predicate
     * @param classEntity
     * @return
     */
    public List<Entity> findWhere(@NotNull String predicate, @NotNull Class classEntity) {
        //Récupération des métadonnées de la table.
        String tableName = EntityMetadata.getTableNameEntity(classEntity);
        Map<String, Class> structure = new TreeMap<String, Class>(EntityMetadata.getStructureEntity(classEntity));

        //Construction de la requête.
        String requestString = "SELECT " +
                                structure.keySet()
                                        .stream()
                                        .collect(Collectors.joining(", FROM_TABLE.", "FROM_TABLE.", "")) +
                                " FROM " + tableName + " AS FROM_TABLE " + predicate + ";";

        List<Entity> entitys = new ArrayList<Entity>();
        try {
            //Exécution de la requête.
            Statement request = connection.createStatement();
            ResultSet resultEntitys = request.executeQuery(requestString);

            //Lecture des lignes du résultat de la requête.
            while(resultEntitys.next()) {
                Map<String, Object> entityAttributes = new HashMap<String, Object>();
                for(String attribute : structure.keySet()) {
                    Object value = null;
                    Class type = structure.get(attribute);
                    if (type.equals(Integer.class)) {
                        value = resultEntitys.getInt(attribute);
                        if(resultEntitys.wasNull()) {
                            value = null;
                        }
                    } else if (type.equals(Double.class)) {
                        value = resultEntitys.getDouble(attribute);
                        if(resultEntitys.wasNull()) {
                            value = null;
                        }
                    } else if (type.equals(String.class)) {
                        value = resultEntitys.getString(attribute);
                    } else if (type.equals(Date.class)) {
                        value = resultEntitys.getTimestamp(attribute);
                    }
                    entityAttributes.put(attribute, value);
                }
                Entity entity = EntityMetadata.instanceEntity(classEntity, entityAttributes);
                entitys.add(entity);
            }

            //Fin de la lecture du résultat de la requête.
            resultEntitys.close();
            //Fin de la requête.
            request.close();
        } catch (Exception e) {
            System.err.println("Erreur ! Une requête de sélection a échouée : \"" + requestString + "\" !");
            e.printStackTrace();
            System.exit(1);
        }

        return entitys;
    }

    /**
     * Chercher un unique n-uplet d'une table avec un prédicat.
     *
     * Cette méthode retourne le premier n-uplet trouvé par cette
     * autre méthode :
     * @see fr.ul.miage.ai_airline.orm.ORM#findWhere(String, Class)
     *
     * Si aucun n-uplet n'est trouvé, la méthode retourne null.
     *
     * @param predicate
     * @param classEntity
     * @return
     */
    public Entity findOneWhere(@NotNull String predicate, @NotNull Class classEntity) {
        List<Entity> entitys = findWhere(predicate, classEntity);
        return entitys.size() == 0 ? null : entitys.get(0);
    }

    /**
     * Chercher un unique n-uplet d'une table avec son id.
     *
     * Cette méthode retourne le premier n-uplet trouvé par cette
     * autre méthode :
     * @see fr.ul.miage.ai_airline.orm.ORM#findOneWhere(String, Class)
     *
     * Si aucun n-uplet n'est trouvé, la méthode retourne null.
     *
     * @param id
     * @param classEntity
     * @return
     */
    public Entity findOne(@NotNull Integer id, @NotNull Class classEntity) {
        return findOneWhere("WHERE ID = " + id, classEntity);
    }

    /**
     * Chercher tous les n-uplets d'une table.
     *
     * Cette méthode utilise cette autre méthode, sans prédicat /
     * condition :
     * @see fr.ul.miage.ai_airline.orm.ORM#findWhere(String, Class)
     *
     * Si la table est vide, la méthode retourne une liste vide.
     */
    public List<Entity> findAll(@NotNull Class classEntity) {
        return findWhere("", classEntity);
    }

    /**
     * Compter le count de n-uplets d'une table en précisant
     * un prédicat / une condition.
     *
     * @see fr.ul.miage.ai_airline.orm.ORM#findNative(String)
     *
     * @param predicate
     * @param classEntity
     * @return
     */
    public Integer countWhere(@NotNull String predicate, @NotNull Class classEntity) {
        //Récupération des métadonnées de la table.
        String tableName = EntityMetadata.getTableNameEntity(classEntity);

        //Construction de la requête.
        String requestString = "SELECT COUNT(FROM_TABLE.*) AS COUNT_ENTITYS FROM " + tableName + " AS FROM_TABLE " + predicate + ";";

        //Exécution de la requête.
        Integer countEntitys = ((Long) findNative(requestString).get(0).get("COUNT_ENTITYS")).intValue();

        return countEntitys;
    }

    /**
     * Compter le count total de n-uplets d'une table.
     *
     * Cette méthode utilise cette autre méthode, sans prédicat :
     * @see fr.ul.miage.ai_airline.orm.ORM#countWhere(String, Class)
     *
     * @param classEntity
     * @return
     */
    public Integer countAll(@NotNull Class classEntity) {
        return countWhere("", classEntity);
    }

    /**
     * Faire persister un n-uplet, en précisant le n-uplet.
     *
     * Faire persister <=> insérer ou mettre à jour.
     * 
     * @see fr.ul.miage.ai_airline.orm.ORM#add(Entity)
     * @see fr.ul.miage.ai_airline.orm.ORM#update(Entity)
     */
    public void save(@NotNull Entity entity) {
        //Mode : insertion ou mise à jour ?
        boolean modeInsertion = entity.getId() == null;
        //Cas insertion.
        if(modeInsertion) {
            add(entity);
        //Cas mise à jour.
        } else {
            update(entity);
        }
    }

    /**
     * Insérer un n-uplet, en précisant le n-uplet.
     */
    public void add(@NotNull Entity entity) {
        //Récupération des métadonnées de la table.
        Class classEntity = entity.getClass();
        String tableName = EntityMetadata.getTableNameEntity(classEntity);
        Map<String, Class> structure = EntityMetadata.getStructureEntity(classEntity);

        //Construction de la requête.
        String requestString = "INSERT INTO " + tableName + "(";
        //Colonnes.
        for(String attribute : structure.keySet()) {
            if(!attribute.equals("ID")) {
                requestString += attribute + ",";
            }
        }
        requestString = requestString.substring(0, requestString.length() - 1) + ") VALUES (" ;
        //Values.
        for(String attribute : structure.keySet()) {
            if(!attribute.equals("ID")) {
                Object value = entity.get(attribute);
                if(value == null) {
                    requestString += "NULL,";
                } else {
                    requestString += "'" + value + "',";
                }
            }
        }
        requestString = requestString.substring(0, requestString.length() - 1) + ");";

        try {
            //Execution de la requête.
            PreparedStatement request = connection.prepareStatement(requestString, Statement.RETURN_GENERATED_KEYS);
            request.executeUpdate();

            //Validation de la transaction.
            connection.commit();

            //On récupère l'id généré pour le nouvel n-uplet.
            ResultSet generatedKeys = request.getGeneratedKeys();
            generatedKeys.next();
            entity.set("ID", generatedKeys.getInt("ID"));

            //Fin de la lecture des clés générées.
            generatedKeys.close();
            //Fin de la requête.
            request.close();
        } catch (Exception e) {
            System.err.println("Erreur ! Une requête d'insertion a échouée : \"" + requestString + "\" !");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Mettre à jour un n-uplet, en précisant le n-uplet.
     */
    public void update(@NotNull Entity entity) {
        //Récupération des métadonnées de la table.
        Class classEntity = entity.getClass();
        String tableName = EntityMetadata.getTableNameEntity(classEntity);
        Map<String, Class> structure = EntityMetadata.getStructureEntity(classEntity);

        //Construction de la requête.
        String requestString = "UPDATE " + tableName + " SET ";
        //Nouvelles values.
        for(String attribute : structure.keySet()) {
            if(!attribute.equals("ID")) {
                Object value = entity.get(attribute);
                if(value == null) {
                    requestString += attribute + " = NULL,";
                } else {
                    requestString += attribute + " = '" + value + "',";
                }
            }
        }
        requestString = requestString.substring(0, requestString.length() - 1) + " WHERE ID = " + entity.getId() + ";";

        try {
            //Execution de la requête.
            Statement request = connection.createStatement();
            request.executeUpdate(requestString);

            //Validation de la transaction.
            connection.commit();

            //Fin de la requête.
            request.close();
        } catch (Exception e) {
            System.err.println("Erreur ! Une requête de mise à jour a échouée : \"" + requestString + "\" !");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Supprimer des n-uplets d'une table en précisant
     * un prédicat / une condition.
     *
     * @param predicate
     * @param classEntity
     */
    public void removeWhere(@NotNull String predicate, @NotNull Class classEntity) {
        //Récupération des métadonnées de la table.
        String tableName = EntityMetadata.getTableNameEntity(classEntity);

        //Construction de la requête.
        String requestString = "DELETE FROM " + tableName + " " + predicate + " ;";

        try {
            //Exécution de la requête.
            Statement request = connection.createStatement();
            request.executeUpdate(requestString);

            //Validation de la transaction.
            connection.commit();

            //Fin de la requête.
            request.close();
        } catch (Exception e) {
            System.err.println("Erreur ! Une requête de suppression a échouée : \"" + requestString + "\" !");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Supprimer un unique n-uplet, en précisant le n-uplet.
     *
     * @see fr.ul.miage.ai_airline.orm.ORM#removeWhere(String, Class)
     * 
     * @param entity
     */
    public void removeOne(@NotNull Entity entity) {
        removeWhere("WHERE ID = " + entity.getId(), entity.getClass());
    }

    /**
     * Supprimer tous les n-uplets d'une table.
     *
     * Cette méthode utilise cette autre méthode, sans prédicat /
     * condition :
     *  @see fr.ul.miage.ai_airline.orm.ORM#removeWhere(String, Class)
     *
     * @param classEntity
     */
    public void removeAll(@NotNull Class classEntity) {
        removeWhere("", classEntity);
    }

    /**
     * Réinitialiser une séquence d'id d'une table à une value
     * de départ.
     *
     * @param classEntity
     */
    public void resetSequenceId(int idDebut, @NotNull Class classEntity) {
        //Récupération des métadonnées de la table.
        String tableName = EntityMetadata.getTableNameEntity(classEntity);

        //Contruction de la requête.
        String requestString = "ALTER SEQUENCE " + tableName.toLowerCase() + "_id_seq RESTART WITH " + idDebut + ";";

        try {
            //Exécution de la requête.
            Statement request = connection.createStatement();
            request.executeUpdate(requestString);

            //Validation de la transaction.
            connection.commit();

            //Fin de la requête.
            request.close();
        } catch (Exception e) {
            System.err.println("Erreur ! Une requête de réinitialisation de séquence d'id a échouée : \"" + requestString + "\" !");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Réinitialiser une séquence d'id d'une table à une value
     * de départ de 1.
     *
     * @see fr.ul.miage.ai_airline.orm.ORM#resetSequenceId(int, Class)
     * 
     * @param classEntity
     */
    public void resetSequenceIdAt1(@NotNull Class classEntity) {
        resetSequenceId(1, classEntity);
    }

    /**
     * Supprimer tous les n-uplets d'une table, et réinitialiser
     * la séquence de ses ids à 1.
     *
     * @see fr.ul.miage.ai_airline.orm.ORM#removeAll(Class)
     * @see fr.ul.miage.ai_airline.orm.ORM#resetSequenceIdAt1(Class)
     * 
     * @param classEntity
     */
    public void reset(@NotNull Class classEntity) {
        removeAll(classEntity);
        resetSequenceIdAt1(classEntity);
    }
}