package fr.ul.miage.ai_airline.agent;


        import jade.core.Agent;
        import jade.core.behaviours.TickerBehaviour;
        import jade.lang.acl.ACLMessage;
        import org.json.JSONException;
        import org.json.JSONObject;

public class ConsultationAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println("Je suis l'agent de la compagnie aérienne qui écoute les requêtes de consultation : " +
                getLocalName() + " appelé aussi " + getAID().getName());


        addBehaviour(new TickerBehaviour(this, 1000) {
            @Override
            protected void onTick() {
                System.out.println("Tick listening consultation");

                // Wait for incoming message
                ACLMessage msg = receive();
                if (msg != null) {
                    System.out.println("Listention consultation agent === Message received from " + msg.getSender().getLocalName());
//                  System.out.println("Internal agent === Message content is : " + getLocalName() + " <- " + msg.getContent());
                    try {
                        JSONObject json = new JSONObject(msg.getContent());
                        String idResquest = json.getString("idRequete");
                        System.out.println(idResquest);
                    } catch (JSONException e) {
                        System.err.println("AGENT LISTEN CONSULTATION CRASH");
                        System.out.println(msg.getContent());
                        e.printStackTrace();
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


