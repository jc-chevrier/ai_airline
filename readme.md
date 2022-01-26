## Solution
Projet `AI Airline`.

#### Description
Projet simulant une compagnie aérienne avec des assistants intelligents.
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

#### Exécution

1. Se rendre dans la branche `git` `example`, ou dans la branche `git` `prod` du projet.

2. Renseigner dans votre variable d'environnement `PATH`, le chemin du répertoire `/bin`
   de votre `Java`, et le chemin du répertoire `/bin` de votre `PostgreSQL`.

3. Renseigner dans votre variable d'environnement `CLASSPATH`, le répertoire `/bin` de `Java`,
   le chemin du fichier `lib/jade.jar` de `Jade`, et le répertoire courant `.`.

4. Renseigner votre configuration `PostgreSQL` pour la base de données du projet dans
   le fichier: `bin/[...]/configuration/database.properties`.

5. Démarrer votre serveur `PostgreSQL`.

6. Aller dans le répertoire `bin/[...]/cmd/` du projet, et exécuter dans ce répertoire `database_initializer.sh` ou `database_initializer.bat`
   en fonction de votre OS. Cela va créer la base de données `PostgreSQL` du projet, vous n'avez pas besoin de la créer vous-même avant.

7. Exécuter `ai_airline.sh` ou `ai_airline.bat` en fonction de votre
   OS. <br>
    - Les exécutables peuplent la base de données `PostgreSQL` avec un jeu de données.<br>
    - Le jeu de données est régénéré à chaque nouvelle exécution, sauf modification dans les fichiers de configuration.

#### Branches `Git`

- `example`: branche contenant les classes `java` compilées, et les exécutables
  `bash` et `batch` du projet, <i><b>et contenant des mocks simulant les agents de l'assistant client
  (pratique pour les tests)</b></i>. Les mocks se lancent directement en parallèle des agents de la compagnie, au lancement des exécutables.

- `prod`: branche contenant les classes `java` compilées, et les exécutables
  `bash` et `batch` du projet, <i><b>sans les mocks des agents de l'assistant client</b></i>.

- `dev`: branche contenant les sources du projet: les classes `java` dans
  `src/`, et les utilitaires exécutables dans `cmd/`.


#### Problème de résolution de dépendence

La dépendance de `Jade` déclarée dans le `pom.xml` n'est plus résolut par
`Maven`. Si vous souhaitez utiliser la branche `dev`, vous devez renseigner la
dépendance vous-même dans le répertoire de votre pc `[...]/.m2/com/` qui est
le répertoire des dépendances centralisées sur votre pc. Vous pouvez trouver la
dépendance de `Jade` en question dans le répertoire `jade_dependency/` du répertoire
`git`.

#### Configuration des paramètres techniques

Pour paramétrer les paramètres techniques du projet, tels que le `mode debug`,
il suffit de se rendre dans les fichiers de configuration. Dans la branche `dev`:
`src/main/resources/[...]/configuration/`, dans les branches `example` et `prod`:
`bin/[...]/configuration/`.

#### Configuration des données globales

Pour paramétrer les données globales de la compagnie, telles que le solde de la compagnie,
il suffit de se rendre dans la table `public.global` de la base de données `PostgreSQL` du projet.