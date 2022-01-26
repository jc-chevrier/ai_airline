## Solution
`AI Airline`.

#### Description
Solution simulant une compagnie aérienne avec des assistants intelligents.
<br>
Une compagnie aérienne est définie ici comme une entité proposant des vols, et permettant
de réserver et d'acheter des places dans ces vols.

#### Equipe

- BONCI Jérémy.
- CHEVRIER Jean-Christophe.
- CRINON Nicolas.

#### Langages, librairies

- `Java 17`.
- `Maven`.
- `Jade`.
- `PostgreSQL`.

#### Installation et exécution

1. Se rendre dans la branche `git` `example`, ou dans la branche `git` `prod` de la solution.

2. Renseigner dans votre variable d'environnement `PATH`, le chemin du répertoire `bin/`
   de votre `Java`, et le chemin du répertoire `bin/` de votre `PostgreSQL`.

3. Renseigner dans votre variable d'environnement `CLASSPATH`, le répertoire `bin/` de `Java`,
   le chemin du fichier `lib/jade.jar` de `Jade`, et le répertoire courant `.`.

4. Renseigner votre configuration `PostgreSQL` pour la base de données de la solution dans
   le fichier: `bin/fr/[...]/configuration/database.properties`.

5. Démarrer votre serveur `PostgreSQL`.

6. Aller dans le répertoire `bin/fr/[...]/cmd/` de la solution, et exécuter dans ce répertoire `database_initializer.sh` ou `database_initializer.bat`
   en fonction de votre OS. Cela va créer la base de données `PostgreSQL` de la solution, vous n'avez pas besoin de la créer vous-même avant.

7. Exécuter `ai_airline.sh` ou `ai_airline.bat` en fonction de votre
   OS. <br>
    - Les exécutables peuplent la base de données `PostgreSQL` avec un jeu de données.<br>
    - Le jeu de données est régénéré à chaque nouvelle exécution, sauf modification dans les fichiers de configuration.

#### Branches `Git`

- `example`: branche contenant les classes `java` compilées, et les exécutables
  `bash` et `batch` de la solution, <i><b>et contenant des mocks simulant les agents de l'assistant client
  (pratique pour les tests)</b></i>. Les mocks se lancent directement en parallèle des agents de la compagnie, au lancement des exécutables.

- `prod`: branche contenant les classes `java` compilées, et les exécutables
  `bash` et `batch` de la solution, <i><b>sans les mocks des agents de l'assistant client</b></i>.

- `dev`: branche contenant les sources de la solution: les classes `java` dans
  `src/`, et les utilitaires exécutables dans `cmd/`.


#### Problème de résolution de dépendence

La dépendance de `Jade` déclarée dans le `pom.xml` n'est plus résolut par
`Maven`. Si vous souhaitez utiliser la branche `dev`, vous devez renseigner la
dépendance vous-même dans le répertoire de votre pc `[...]/.m2/com/` qui est
le répertoire des dépendances centralisées sur votre pc. Vous pouvez trouver la
dépendance de `Jade` en question dans le répertoire `jade_dependency/` du répertoire
`git`.

#### Configuration des paramètres techniques

Pour paramétrer les paramètres techniques de la solution, tels que le `mode debug`,
il suffit de se rendre dans les fichiers de configuration. Dans la branche `dev`:
`src/main/resources/[...]/configuration/`, dans les branches `example` et `prod`:
`bin/fr/[...]/configuration/`.

#### Configuration des données globales

Pour paramétrer les données globales de la compagnie, telles que le solde de la compagnie,
il suffit de se rendre dans la table `public.global` de la base de données `PostgreSQL` de la solution.

#### Arborescence sur`dev`

Voici comment se présente l'arborescence de la solution sur la branche `dev` pour l'essentiel:

    cmd/                                    Exécutables utilitaires de la solution.
        database_initializer.(sh|bat)       Exécutables pour recréer la base de données, recréer ses tables et y insérer les lignes pour les tables référentielles.
        database_query.(sh|bat)             Exécutables pour faire des requêtes sur la base de données.

    src/main/                               
        java/[...]/                         Classes java de la solution.
            agent/                          Agents jade de la compagnie aérienne.
            configuration/                  Interface avec les fichiers de configuration.
            data_structure/                 Entités des données de la compagnie aérienne.
            dataset/                        Programmes du jeu de données.
            mock_agent/                     Agents jade simulant l'assitant client.
            orm/                            ORM : sucrouche sur le driver de PostreSQL.
            tool/                           Utilitaires.
            ...

        resources/[...]/
            configuration/                  Fichiers de configuration de la solution.    
                agent.properties            Fichier de la configuration des agents jade.
                database.properties         Fichier de la configuration de la base de données.
                global.properties           Fichier de la configuration globale.

        sql/[...]/
            initialization/                 Scripts d'initialisation de la base de données.
                database_initializer.sql    Script SQL de destruction création de la base de données.
                tables_builder.sql          Script SQL d'isnertions des ligens dans les tables référentielles de la base de données.
                tables_initializer.sql      Script SQL de création des tables de la base de données.