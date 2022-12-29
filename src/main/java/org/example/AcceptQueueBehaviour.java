package org.example;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AcceptQueueBehaviour extends Behaviour {

    private double delta;
    private double x;
    private int paramCount = 0;

    private final MessageTemplate mt = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
            MessageTemplate.or(
                    MessageTemplate.MatchProtocol("send x"),
                    MessageTemplate.MatchProtocol("send delta")));

    public AcceptQueueBehaviour(Agent myAgent) {
        this.myAgent = myAgent;
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive(mt);
        if (msg != null) {

            if (msg.getProtocol().equals("send x")) {
                x = Double.parseDouble(msg.getContent());
            }
            if (msg.getProtocol().equals("send delta")) {
                delta = Double.parseDouble(msg.getContent());
            }

            paramCount++;

            if (paramCount > 1) {
                ACLMessage m = msg.createReply();
                m.setPerformative(ACLMessage.AGREE);
                m.setProtocol("queue");
                myAgent.send(m);

                System.out.println("\n" + myAgent.getLocalName() + " accept queue from " + msg.getSender().getLocalName());
                System.out.println(myAgent.getLocalName() + ": x = " + x + "; delta = " + delta + "\n");


                myAgent.addBehaviour(new SendRequestBehaviour(myAgent, x, delta));
                paramCount = 0;
            }
        } else {
            block();
        }
    }

    @Override
    public boolean done() {
        return false;
    }
}
