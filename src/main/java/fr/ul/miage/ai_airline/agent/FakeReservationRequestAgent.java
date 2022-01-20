package fr.ul.miage.ai_airline.agent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class FakeReservationRequestAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println("Je un agent qui envoie des requêtes de réservation de vol(s) : " +
                getLocalName() + " appelé aussi " + getAID().getName());


        addBehaviour(new TickerBehaviour(this, 3000) {
            @Override
            protected void onTick() {
                String receivername = "internal_agent_assistant";
                System.out.println("External agent === Sending message to "+receivername);

//                Pour rechercher vol :
//                {
//                    "idRequete": "id",        // identifiant de requete
//                    "dateDepart": "date",     // date de départ souhaitée
//                    "prix": "double",         // fourchette de prix
//                    "destination": "string",  // destination souhaitée (soit thème soit destination)
//                    "classe": "string",       // classe souhaité (1er classe, éco)
//                }

                String flightrequest =
                        "{"+
                                "\"idRequete\": \"id-azedf\","+     // identifiant de requete
                                "\"dateDepart\": \"date\","+         // date de départ souhaitée
                                "\"prix\": \"double\","+             // fourchette de prix
                                "\"destination\": \"string\","+      // destination souhaitée (soit thème soit destination)
                                "\"classe\": \"string\""+            // classe souhaité (1er classe, éco)
                                "}";

                // Send message
                ACLMessage request = new ACLMessage(ACLMessage.INFORM);
//                request.setContent("Ping");
                request.setContent(flightrequest);
                request.addReceiver(new AID(receivername, AID.ISLOCALNAME));
                send(request);
                // Listening to response
                ACLMessage response = receive();
                if (response != null) {
                    System.out.println("External agent === Message received from " + response.getSender().getLocalName());
                    System.out.println("External agent === Message content is : " + getLocalName() + " <- " + response.getContent());
                    ACLMessage reply = response.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent("Pong");
                    send(reply);
                }
                block();


            }
        });

    }
}

