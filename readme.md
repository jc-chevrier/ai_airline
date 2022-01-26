## Solution
Projet AI Airline

#### Description 
Projet simulant une compagnie aérienne avec
des assistants intelligents.

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

2. Renseigner la configuration de la base de données `PostgreSQL` du projet dans 
   `bin/[...]/configuration/database.properties`.

3. Exécuter `ai_airline.sh` ou `ai_airline.bat` en fonction de votre
OS. <br><br>
Les exécutables créent et peuplent la base de données `PostgreSQL` du projet si elle
n'existe pas encore, vous n'avez pas besoin de la créer vous-même.

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
dépendance de `Jade` à copier dans le répertoire `jade_depenecy/` du répertoire 
`git`.