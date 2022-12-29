package org.example;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class SendRequestBehaviour extends Behaviour {

    private final Agent myAgent;
    private double x;
    private double delta;
    private boolean calculateIsEnd = false;
    private int cycleCount = 0;
    private final double[] sumY = {0, 0, 0};
    private int requestsCount;
    List<AID> agents;

    private final MessageTemplate mt = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.INFORM),
            MessageTemplate.MatchProtocol("calculate"));


    private AID getNextName(){
        int i = new Random().nextInt(3);
        while (agents.get(i).equals(myAgent.getAID())) {
            i = new Random().nextInt(3);
        }
        return agents.get(i);
    }

    public SendRequestBehaviour(Agent myAgent, double x, double delta) {
        this.myAgent = myAgent;
        this.x = x - delta;
        this.delta = delta;
        this.agents = DfHelper.findAgents(myAgent, "agent");
    }

    @Override
    public void onStart() {
        ACLMessage m = new ACLMessage(ACLMessage.REQUEST);
        m.setProtocol("calculate");
        agents.forEach(m::addReceiver);
        m.setContent(""+x);
        myAgent.send(m);
        requestsCount = agents.size();
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive(mt);

        if (msg != null) {
            System.out.println(myAgent.getLocalName() + " get < " + msg.getContent() + " > from " + msg.getSender().getLocalName());
                sumY[cycleCount] = sumY[cycleCount] + Double.parseDouble(msg.getContent());

            requestsCount --;

            if (requestsCount<=0) {
                System.out.println("\n" + myAgent.getLocalName() + ": intermediate result: " + Arrays.toString(sumY) + "\n");
                cycleCount++;
                if (cycleCount <= sumY.length-1) {
                    x = x + delta;
                    onStart();
                } else {
                    func();
                }
            }

        } else {
            block();
        }
    }

    private void func() {
        System.out.println("\n" + myAgent.getLocalName() + ": result: ");
        System.out.println("\tx =\t" + (x) + "\t" + (x+delta) + "\t" + (x+2*delta));
        System.out.println("\ty =\t" + sumY[0] + "\t" + sumY[1] + "\t" + sumY[2]);

        if (sumY[1] < sumY[0] && sumY[1] < sumY[2]) {
            x = x - delta;
            delta = delta / 2;
        } else if (sumY[0] < sumY[1] && sumY[0] < sumY[2]) {
            x = x - 2 * delta;
        }

        System.out.println("\tminX = " + x + "\tdelta =" + delta + "\tminY = " + Arrays.stream(sumY).min().getAsDouble() + "\n");

        if (delta > 0.0001) {
            myAgent.addBehaviour(new SendQueueBehavior(myAgent, getNextName(), x, delta));
            myAgent.removeBehaviour(this);
        } else {
            calculateIsEnd = true;
        }
    }

    @Override
    public int onEnd() {
        System.out.println("----------RESULT----------");
        System.out.println("x = " + x);
        System.out.println("delta = " + delta);
        if (Arrays.stream(sumY).min().isPresent()) {
            System.out.println("Y = " + Arrays.stream(sumY).min().getAsDouble());
        } else {
            System.out.println("Y err");
        }
        System.out.println("Last agent is " + myAgent.getLocalName());

        return 0;
    }

    @Override
    public boolean done() {
        return calculateIsEnd;
    }
}
