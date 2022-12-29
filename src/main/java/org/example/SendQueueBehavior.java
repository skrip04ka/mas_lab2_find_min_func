package org.example;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class SendQueueBehavior extends Behaviour {
    private final Agent myAgent;
    private final AID nextAgent;
    private boolean queueSend = false;
    private final double x;
    private final double delta;

    private final MessageTemplate mt = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.AGREE),
            MessageTemplate.MatchProtocol("queue"));
    public SendQueueBehavior(Agent myAgent, AID nextAgent, double x, double delta) {
        this.myAgent = myAgent;
        this.nextAgent = nextAgent;
        this.x = x;
        this.delta = delta;
    }

    @Override
    public void onStart() {
        ACLMessage m = new ACLMessage(ACLMessage.PROPOSE);
        m.addReceiver(nextAgent);

        m.setProtocol("send x");
        m.setContent(""+x);
        myAgent.send(m);

        m.setProtocol("send delta");
        m.setContent(""+delta);
        myAgent.send(m);

        System.out.println("\n" + myAgent.getLocalName() + " send queue to " +nextAgent.getLocalName());
        System.out.println(myAgent.getLocalName() + ": x = " + x + "; delta = " + delta);
        myAgent.doWait(1000);
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive(mt);
        if (msg != null) {
            System.out.println(myAgent.getLocalName() + " received confirmation from " + msg.getSender().getLocalName() + "\n");
            queueSend = true;
        } else {
            myAgent.doWait(1000);
            System.out.println("\nTime is over. Resending ...");
            onStart();
        }
    }


    @Override
    public boolean done() {
        return queueSend;
    }
}
