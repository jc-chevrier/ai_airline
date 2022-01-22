package fr.ul.miage.ai_airline.agent;


        import jade.core.Agent;
        import jade.core.behaviours.TickerBehaviour;
        import jade.lang.acl.ACLMessage;
        import jade.lang.acl.MessageTemplate;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.time.LocalDateTime;
        import java.time.format.DateTimeFormatter;

public class ConsultationAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println("Je suis l'agent de la compagnie aérienne qui écoute les requêtes de consultation : " +
                getLocalName() + " appelé aussi " + getAID().getName());


        addBehaviour(new TickerBehaviour(this, 1000) {
            @Override
            protected void onTick() {
                System.out.println("Tick listening consultation");

                // Wait for incoming message of fipa-type ACLMessage.REQUEST
                MessageTemplate onlyREQUEST = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
                ACLMessage msg = receive(onlyREQUEST);
                if (msg != null) {
                    System.out.println("Listention consultation agent === Message received from " + msg.getSender().getLocalName());
//                  System.out.println("Internal agent === Message content is : " + getLocalName() + " <- " + msg.getContent());
                    try {
                        System.out.println("----------- BEGIN ----------------");
                        System.out.println(msg.getContent());
                        System.out.println("----------- END ----------------");
                        JSONObject json = new JSONObject(msg.getContent());
                        String idResquest = json.getString("idRequete");
                        System.err.println("LECTURE");
                        System.out.println(idResquest);
                        String takeoffDate = json.getString("dateDepart");
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                        LocalDateTime dateTime = LocalDateTime.parse(takeoffDate, formatter);
                        // string : dateTime.format(formatter)  == 2022-01-28 11:30
                        // datetime == 2022-01-28T11:30


                    } catch (JSONException e) {
                        System.err.println("AGENT LISTEN CONSULTATION CRASH");
                        System.err.println(msg.getContent());
                        e.printStackTrace();
                        System.exit(1);
                    }

                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent("Pong");
                    send(reply);
                }
                block();

            }
        });

    }
}


