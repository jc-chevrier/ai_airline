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

#### Exécution

1. Se rendre dans la branche `example`, ou dans la branche `prod`.

2. Exécuter `ai_airline.sh` ou `ai_airline.bat` en fonction de votre
OS.

#### Branches

- `example`: branche contenant les classes `java` compilées, et les exécutables
  `bash` et `batch` du projet, <i><b>set contenant des mocks simulant les agents de l'assistant client 
  (pratique pour les tests)</b></i>.

- `prod`: branche contenant les classes `java` compilées, et les exécutables
  `bash` et `batch` du projet, <i><b>sans les mocks des agents de l'assistant client</b></i>.

- `dev`: branche contenant les sources du projet: les classes `java` dans
  `/src`, et les utilitaires exécutables dans `/cmd`.

#### Problème de résolution de dépendence

La dépendance de `Jade` déclarée dans le `pom.xml` n'est plus résolut par 
maven, si vous souhaitez utilisé la branche dev, vous devez la renseigner 
vous-même dans le répertoire de votre pc [...]/.m2/com/ qui est le répertoire
des dépendances centralisées sur votre pc.