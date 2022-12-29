package org.example;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AcceptRequestBehaviour extends Behaviour {

    private Function f;

    @Override
    public void onStart() {
        f = new Function(myAgent.getLocalName());
    }

    public AcceptRequestBehaviour(Agent myAgent) {
        this.myAgent = myAgent;
    }

    @Override
    public void action() {

        ACLMessage msg = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
        if (msg != null) {
            System.out.println(myAgent.getLocalName() + " accept request < " + msg.getContent() + " > from " + msg.getSender().getLocalName());
            ACLMessage m = msg.createReply();
            m.setPerformative(ACLMessage.INFORM);
            m.setContent("" + f.getResult(Double.parseDouble(msg.getContent())));
            myAgent.send(m);
        } else {
            block();
        }
    }

    @Override
    public boolean done() {
        return false;
    }
}
