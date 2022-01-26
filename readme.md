## Solution
Projet `AI Airline`.

#### Description 
Projet simulant une compagnie aérienne avec des assistants intelligents. 
<br>
Une compagnie aérienne est définie ici comme une entité proposant des vols, et permettant
de réserver et d'acheter des places dans ces vols.

#### Equipe

- BONCI Jérémy
- CHEVRIER Jean-Christophe
- CRINON Nicolas

#### Technologies

- `Java 17` 
- `Maven` 
- `Jade`
- `PostgreSQL`

#### Exécution

1. Se rendre dans la branche `example`, ou dans la branche `prod`.

2. Renseigner votre configuration `PostgreSQL` pour la base de données du projet dans 
   `bin/[...]/configuration/database.properties`.

3. Exécuter `ai_airline.sh` ou `ai_airline.bat` en fonction de votre
OS. <br>
Les exécutables créent la base de données `PostgreSQL` du projet, et la peuplent avec un jeu de données, vous n'avez pas besoin de la créer vous-même avant.

#### Branches

- `example`: branche contenant les classes `java` compilées, et les exécutables
  `bash` et `batch` du projet, <i><b>set contenant des mocks simulant les agents de l'assistant client 
  (pratique pour les tests)</b></i>.

- `prod`: branche contenant les classes `java` compilées, et les exécutables
  `bash` et `batch` du projet, <i><b>sans les mocks des agents de l'assistant client</b></i>.

- `dev`: branche contenant les sources du projet: les classes `java` dans
  `src/`, et les utilitaires exécutables dans `cmd/`.

  
#### Problème de résolution de dépendence

La dépendance de `Jade` déclarée dans le `pom.xml` n'est plus résolut par 
maven, si vous souhaitez utiliser la branche `dev`, vous devez renseigner la 
dépendance vous-même dans le répertoire de votre pc `[...]/.m2/com/` qui est
le répertoire des dépendances centralisées sur votre pc. Vous pouvez trouver
dépendance de `Jade` à copier dans le répertoire `jade_dependency/` du répertoire 
`git`.

#### Configuration des paramètres techniques

Pour paramétrer les paramètres techniques du projet, tels que le `mode debug`,
il suffit de se rendre dans les fichiers de configuration. Dans la branche `dev`: 
`src/main/resources/[...]/configuration`, dans les branches `example` et `prod`:
`bin/[...]/configuration/`.

#### Configuration des données globales

Pour paramétrer les données globales de la compagnie, tels que le solde de la compagnie, 
il suffit de se rendre dans la table `public.global` de la base de données `PotgreSQL` du projet.