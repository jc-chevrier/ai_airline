package fr.ul.miage.ai_airline.mock_agent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Mock pour simuler l'agent de l'assistant
 * client qui envoie des requêtes de
 * recherche de vol.
 */
public class MockSearchRequestAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println("Je un agent qui envoie des requêtes de consultation de vols : " +
                getLocalName() + " appelé aussi " + getAID().getName());


        addBehaviour(new TickerBehaviour(this, 3000) {
            @Override
            protected void onTick() {
                String receivername = "agent_consultation";
                System.out.println("Fake consultation request agent === Sending message to "+receivername);

//                Pour rechercher vol :
//                {
//                    "idRequete": "id",        // identifiant de requete
//                    "dateDepart": "date",     // date de départ souhaitée
//                    "prix": "double",         // fourchette de prix
//                    "destination": "string",  // destination souhaitée (soit thème soit destination)
//                    "classe": "string",       // classe souhaité (1er classe, éco)
//                }

                String str = "2022-01-28 11:30:30";
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
                String dateDecollage = dateTime.format(formatter);
                String flightrequest =
                        "{"+
                                "\"idRequete\": \"id-azedf\","+     // identifiant de requete
                                "\"dateDepart\": \""+dateDecollage+"\","+         // date de départ souhaitée
                                "\"prixHaut\": \"200.00\","+             // fourchette de prix
                                "\"prixBas\": \"10.00\","+             // fourchette de prix
                                "\"destination\": \"Barcelone (Espagne)\","+      // destination souhaitée (soit thème soit destination)
                                "\"classe\": \"Economique\""+            // classe souhaité (1er classe, éco)
                                "}";

                // Send response
                ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                request.setContent(flightrequest);
                request.addReceiver(new AID(receivername, AID.ISLOCALNAME));
                send(request);
                MessageTemplate onlyINFORM = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
                // Listening to response
                ACLMessage response = receive(onlyINFORM);
                if (response != null) {
                    System.out.println("External agent === Message received from " + response.getSender().getLocalName());
                    System.out.println("External agent === Message content is : " + getLocalName() + " <- " + response.getContent());
//                    ACLMessage reply = response.createReply();
//                    reply.setPerformative(ACLMessage.INFORM);
//                    reply.setContent("Pong");
//                    send(reply);
                }
                block();


            }
        });

    }
}

