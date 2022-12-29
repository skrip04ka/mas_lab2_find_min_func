package org.example;
import jade.core.Agent;

@AutorunnableAgent(name = "A", count = 3)
public class FunctionAgent extends Agent{

    @Override
    protected void setup() {
        DfHelper.registerAgent(this,  "agent");
        System.out.println("Hello, i'm " + this.getName());

        if (this.getLocalName().equals("A1")) {
            this.addBehaviour(new SendRequestBehaviour(this, -100, 5));
        }
        this.addBehaviour(new AcceptRequestBehaviour(this));
        this.addBehaviour(new AcceptQueueBehaviour(this));



    }

}
