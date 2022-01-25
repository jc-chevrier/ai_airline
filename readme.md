## Solution
Projet AI Airline

### Description 
Projet simulant une compagnie aérienne avec
des assistants intelligents.

### Equipe

- BONCI Jérémy
- CHEVRIER Jean-Christophe
- CRINON Nicolas

### Technologies

- `Java 17` 
- `Maven` 
- `Jade`

### Exécution

1. Se rendre dans la branche `prod`, ou dans la branche `example`.

2. Exécuter `ai_airline.sh` ou `ai_airline.bat` en fonction de votre
OS.

### Branches

- `example`: branche contenant les classes java compilées, et les exécutables
  bash et batch du projet, et contenant des mocks simulant les agents de l'assistant client 
  (pratique pour les tests).

- `prod`: branche contenant les classes java compilées, et les exécutables
  bash et batch du projet, sans les mocks des agents de l'assistant client.

- `dev`: branche contenant les sources du projet: les classes java dans
  `/src`, et les utilitaires exécutables dans `/cmd`.