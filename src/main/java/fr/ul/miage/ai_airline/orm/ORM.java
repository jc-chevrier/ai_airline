package fr.ul.miage.ai_airline.orm;

import fr.ul.miage.ai_airline.Main;
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
 *
 * @author CHEVRIER, HADJ MESSAOUD, LOUGADI
 */
public class ORM {
    //Configuration de la connexion à la base de données.
    private Properties configuration;
    //Connexion à la base de données.
    private Connection connexion;
    //Singleton.
    private static ORM singletonORM;
    //Nom du fichier de configuration.
    public static String CONFIGURATION_FILENAME = "./configuration/database_configuration.properties";

    private ORM() {
        chargerConfiguration();
        connecter();
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
     * Charger la configuration de la connexion
     * à la base de données.
     */
    private void chargerConfiguration() {
        configuration = new Properties();
        try {
            configuration.load(Main.class.getResourceAsStream(CONFIGURATION_FILENAME));
        } catch (IOException e) {
            System.err.println("Erreur ! Problème au cours du chargement de la configuration de la base de données !");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Connecter l'application à la base de données.
     */
    private void connecter() {
        try {
            Class.forName("org.postgresql.Driver");
            connexion = DriverManager.getConnection("jdbc:postgresql://" +
                                                    configuration.get("hote") + ":" + configuration.get("port") + "/" +
                                                    configuration.get("baseDeDonnees"),
                                                    (String) configuration.get("utilisateur"), (String) configuration.get("motDePasse"));
            connexion.setAutoCommit(false);
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
     * @param requeteString
     * @return
     */
    public List<Map<String, Object>> chercherNUplets(@NotNull String requeteString) {
        List<Map<String, Object>> nUplets = new ArrayList<Map<String, Object>>();

        try {
            //Exécution de la requête.
            Statement requete = connexion.createStatement();
            ResultSet resultatNUplets = requete.executeQuery(requeteString);

            //Récupération des libellés des attributs du résultat.
            ResultSetMetaData resultatMetadonnees = resultatNUplets.getMetaData();
            List<String> attributs = new ArrayList<String>();
            int nombreAttributs = resultatMetadonnees.getColumnCount();
            for(int indexAttribut = 1; indexAttribut <= nombreAttributs; indexAttribut++) {
                attributs.add(resultatMetadonnees.getColumnName(indexAttribut).toUpperCase());
            }

            //Lecture du résultat de la requête.
            while(resultatNUplets.next()) {
                Map<String, Object> nUplet = new HashMap<String, Object>();
                nUplets.add(nUplet);
                for(String attribut : attributs) {
                    Object valeur = resultatNUplets.getObject(attribut);
                    nUplet.put(attribut, valeur);
                }
            }

            //Fin de la lecture du résultat de la requête.
            resultatNUplets.close();
            //Fin de la requête.
            requete.close();
        } catch (Exception e) {
            System.err.println("Erreur ! Une requête se sélection a échouée : \"" + requeteString + "\" !");
            e.printStackTrace();
            System.exit(1);
        }

        return nUplets;
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
     * @param predicat
     * @param entiteClasse
     * @return
     */
    public List<Entity> chercherNUpletsAvecPredicat(@NotNull String predicat, @NotNull Class entiteClasse) {
        //Récupération des métadonnées de la table.
        String nomTable = EntityMetadata.getEntiteNomTable(entiteClasse);
        Map<String, Class> structure = new TreeMap<String, Class>(EntityMetadata.getEntiteStructure(entiteClasse));

        //Construction de la requête.
        String requeteString = "SELECT " +
                                structure.keySet()
                                        .stream()
                                        .collect(Collectors.joining(", FROM_TABLE.", "FROM_TABLE.", "")) +
                                " FROM " + nomTable + " AS FROM_TABLE " + predicat + "" +
                                " ORDER BY FROM_TABLE.ID;";

        List<Entity> nUplets = new ArrayList<Entity>();
        try {
            //Exécution de la requête.
            Statement requete = connexion.createStatement();
            ResultSet resultatNUplets = requete.executeQuery(requeteString);

            //Lecture des lignes du résultat de la requête.
            while(resultatNUplets.next()) {
                Map<String, Object> nUpletAttributs = new HashMap<String, Object>();
                for(String attribut : structure.keySet()) {
                    Object valeur = null;
                    Class type = structure.get(attribut);
                    if (type.equals(Integer.class)) {
                        valeur = resultatNUplets.getInt(attribut);
                        if(resultatNUplets.wasNull()) {
                            valeur = null;
                        }
                    } else if (type.equals(Double.class)) {
                        valeur = resultatNUplets.getDouble(attribut);
                        if(resultatNUplets.wasNull()) {
                            valeur = null;
                        }
                    } else if (type.equals(String.class)) {
                        valeur = resultatNUplets.getString(attribut);
                    } else if (type.equals(Date.class)) {
                        valeur = resultatNUplets.getTimestamp(attribut);
                    }
                    nUpletAttributs.put(attribut, valeur);
                }
                Entity nUplet = EntityMetadata.instancierNUplet(entiteClasse, nUpletAttributs);
                nUplets.add(nUplet);
            }

            //Fin de la lecture du résultat de la requête.
            resultatNUplets.close();
            //Fin de la requête.
            requete.close();
        } catch (Exception e) {
            System.err.println("Erreur ! Une requête de sélection a échouée : \"" + requeteString + "\" !");
            e.printStackTrace();
            System.exit(1);
        }

        return nUplets;
    }

    /**
     * Chercher un unique n-uplet d'une table avec un prédicat.
     *
     * Cette méthode retourne le premier n-uplet trouvé par cette
     * autre méthode :
     * @see fr.ul.miage.ai_airline.orm.ORM#chercherNUpletsAvecPredicat(String, Class) 
     *
     * Si aucun n-uplet n'est trouvé, la méthode retourne null.
     *
     * @param predicat
     * @param entiteClasse
     * @return
     */
    public Entity chercherNUpletAvecPredicat(@NotNull String predicat, @NotNull Class entiteClasse) {
        List<Entity> nUplets = chercherNUpletsAvecPredicat(predicat, entiteClasse);
        return nUplets.size() == 0 ? null : nUplets.get(0);
    }

    /**
     * Chercher tous les n-uplets d'une table.
     *
     * Cette méthode utilise cette autre méthode, sans prédicat /
     * condition :
     * @see fr.ul.miage.ai_airline.orm.ORM#chercherNUpletAvecPredicat(String, Class)
     *
     * Si la table est vide, la méthode retourne une liste vide.
     */
    public List<Entity> chercherTousLesNUplets(@NotNull Class entiteClasse) {
        return chercherNUpletsAvecPredicat("", entiteClasse);
    }

    /**
     * Compter le nombre de n-uplets d'une table en précisant
     * un prédicat / une condition.
     *
     * @see fr.ul.miage.ai_airline.orm.ORM#chercherNUplets(String)
     *
     * @param predicat
     * @param entiteClasse
     * @return
     */
    public Integer compterNUpletsAvecPredicat(@NotNull String predicat, @NotNull Class entiteClasse) {
        //Récupération des métadonnées de la table.
        String nomTable = EntityMetadata.getEntiteNomTable(entiteClasse);

        //Construction de la requête.
        String requeteString = "SELECT COUNT(FROM_TABLE.*) AS NOMBRE_NUPLETS FROM " + nomTable + " AS FROM_TABLE " + predicat + ";";

        //Exécution de la requête.
        Integer nombreNUplets = ((Long) chercherNUplets(requeteString).get(0).get("NOMBRE_NUPLETS")).intValue();

        return nombreNUplets;
    }

    /**
     * Compter le nombre total de n-uplets d'une table.
     *
     * Cette méthode utilise cette autre méthode, sans prédicat :
     * @see fr.ul.miage.ai_airline.orm.ORM#compterNUpletsAvecPredicat(String, Class) 
     *
     * @param entiteClasse
     * @return
     */
    public Integer compterTousLesNUplets(@NotNull Class entiteClasse) {
        return compterNUpletsAvecPredicat("", entiteClasse);
    }

    /**
     * Faire persister un n-uplet, en précisant le n-uplet.
     *
     * Faire persister <=> insérer ou mettre à jour.
     * 
     * @see fr.ul.miage.ai_airline.orm.ORM#insererNUplet(Entity)
     * @see fr.ul.miage.ai_airline.orm.ORM#mettreAJourNUplet(Entity)
     */
    public void persisterNUplet(@NotNull Entity nUplet) {
        //Mode : insertion ou mise à jour ?
        boolean modeInsertion = nUplet.getId() == null;
        //Cas insertion.
        if(modeInsertion) {
            insererNUplet(nUplet);
        //Cas mise à jour.
        } else {
            mettreAJourNUplet(nUplet);
        }
    }

    /**
     * Insérer un n-uplet, en précisant le n-uplet.
     */
    public void insererNUplet(@NotNull Entity nUplet) {
        //Récupération des métadonnées de la table.
        Class entiteClasse = nUplet.getClass();
        String nomTable = EntityMetadata.getEntiteNomTable(entiteClasse);
        Map<String, Class> structure = EntityMetadata.getEntiteStructure(entiteClasse);

        //Construction de la requête.
        String requeteString = "INSERT INTO " + nomTable + "(";
        //Colonnes.
        for(String attribut : structure.keySet()) {
            if(!attribut.equals("ID")) {
                requeteString += attribut + ",";
            }
        }
        requeteString = requeteString.substring(0, requeteString.length() - 1) + ") VALUES (" ;
        //Valeurs.
        for(String attribut : structure.keySet()) {
            if(!attribut.equals("ID")) {
                Object valeur = nUplet.get(attribut);
                if(valeur == null) {
                    requeteString += "NULL,";
                } else {
                    requeteString += "'" + valeur + "',";
                }
            }
        }
        requeteString = requeteString.substring(0, requeteString.length() - 1) + ");";

        try {
            //Execution de la requête.
            PreparedStatement requete = connexion.prepareStatement(requeteString, Statement.RETURN_GENERATED_KEYS);
            requete.executeUpdate();

            //Validation de la transaction.
            connexion.commit();

            //On récupère l'id généré pour le nouvel n-uplet.
            ResultSet clesGenerees = requete.getGeneratedKeys();
            clesGenerees.next();
            nUplet.set("ID", clesGenerees.getInt("ID"));

            //Fin de la lecture des clés générées.
            clesGenerees.close();
            //Fin de la requête.
            requete.close();
        } catch (Exception e) {
            System.err.println("Erreur ! Une requête d'insertion a échouée : \"" + requeteString + "\" !");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Mettre à jour un n-uplet, en précisant le n-uplet.
     */
    public void mettreAJourNUplet(@NotNull Entity nUplet) {
        //Récupération des métadonnées de la table.
        Class entiteClasse = nUplet.getClass();
        String nomTable = EntityMetadata.getEntiteNomTable(entiteClasse);
        Map<String, Class> structure = EntityMetadata.getEntiteStructure(entiteClasse);

        //Construction de la requête.
        String requeteString = "UPDATE " + nomTable + " SET ";
        //Nouvelles valeurs.
        for(String attribut : structure.keySet()) {
            if(!attribut.equals("ID")) {
                Object valeur = nUplet.get(attribut);
                if(valeur == null) {
                    requeteString += attribut + " = NULL,";
                } else {
                    requeteString += attribut + " = '" + valeur + "',";
                }
            }
        }
        requeteString = requeteString.substring(0, requeteString.length() - 1) + " WHERE ID = " + nUplet.getId() + ";";

        try {
            //Execution de la requête.
            Statement requete = connexion.createStatement();
            requete.executeUpdate(requeteString);

            //Validation de la transaction.
            connexion.commit();

            //Fin de la requête.
            requete.close();
        } catch (Exception e) {
            System.err.println("Erreur ! Une requête de mise à jour a échouée : \"" + requeteString + "\" !");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Supprimer des n-uplets d'une table en précisant
     * un prédicat / une condition.
     *
     * @param predicat
     * @param entiteClasse
     */
    public void supprimerNUpletsAvecPredicat(@NotNull String predicat, @NotNull Class entiteClasse) {
        //Récupération des métadonnées de la table.
        String nomTable = EntityMetadata.getEntiteNomTable(entiteClasse);

        //Construction de la requête.
        String requeteString = "DELETE FROM " + nomTable + " " + predicat + " ;";

        try {
            //Exécution de la requête.
            Statement requete = connexion.createStatement();
            requete.executeUpdate(requeteString);

            //Validation de la transaction.
            connexion.commit();

            //Fin de la requête.
            requete.close();
        } catch (Exception e) {
            System.err.println("Erreur ! Une requête de suppression a échouée : \"" + requeteString + "\" !");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Supprimer un unique n-uplet, en précisant le n-uplet.
     *
     * @see fr.ul.miage.ai_airline.orm.ORM#supprimerNUpletsAvecPredicat(String, Class) 
     * 
     * @param nUplet
     */
    public void supprimerNUplet(@NotNull Entity nUplet) {
        supprimerNUpletsAvecPredicat("WHERE ID = " + nUplet.getId(), nUplet.getClass());
    }

    /**
     * Supprimer tous les n-uplets d'une table.
     *
     * Cette méthode utilise cette autre méthode, sans prédicat /
     * condition :
     *  @see fr.ul.miage.ai_airline.orm.ORM#supprimerNUpletsAvecPredicat(String, Class)
     *
     * @param entiteClasse
     */
    public void supprimerTousLesNUplets(@NotNull Class entiteClasse) {
        supprimerNUpletsAvecPredicat("", entiteClasse);
    }

    /**
     * Réinitialiser une séquence d'id d'une table à une valeur
     * de départ.
     *
     * @param entiteClasse
     */
    public void reinitialiserSequenceId(int idDebut, @NotNull Class entiteClasse) {
        //Récupération des métadonnées de la table.
        String nomTable = EntityMetadata.getEntiteNomTable(entiteClasse);

        //Contruction de la requête.
        String requeteString = "ALTER SEQUENCE " + nomTable.toLowerCase() + "_id_seq RESTART WITH " + idDebut + ";";

        try {
            //Exécution de la requête.
            Statement requete = connexion.createStatement();
            requete.executeUpdate(requeteString);

            //Validation de la transaction.
            connexion.commit();

            //Fin de la requête.
            requete.close();
        } catch (Exception e) {
            System.err.println("Erreur ! Une requête de réinitialisation de séquence d'id a échouée : \"" + requeteString + "\" !");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Réinitialiser une séquence d'id d'une table à une valeur
     * de départ de 1.
     *
     * @see fr.ul.miage.ai_airline.orm.ORM#reinitialiserSequenceId(int, Class) 
     * 
     * @param entiteClasse
     */
    public void reinitialiserSequenceIdA1(@NotNull Class entiteClasse) {
        reinitialiserSequenceId(1, entiteClasse);
    }

    /**
     * Supprimer tous les n-uplets d'une table, et réinitialiser
     * la séquence de ses ids à 1.
     *
     * @see fr.ul.miage.ai_airline.orm.ORM#supprimerTousLesNUplets(Class)
     * @see fr.ul.miage.ai_airline.orm.ORM#reinitialiserSequenceIdA1(Class) 
     * 
     * @param entiteClasse
     */
    public void reinitialiserTable(@NotNull Class entiteClasse) {
        supprimerTousLesNUplets(entiteClasse);
        reinitialiserSequenceIdA1(entiteClasse);
    }
}