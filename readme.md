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