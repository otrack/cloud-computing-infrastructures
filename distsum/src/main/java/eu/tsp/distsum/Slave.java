package eu.tsp.distsum;

/**
 * The worker node tracks a stream of integers, and maintains a local sum of such values.
 * When an update violates its local constraint, the worker informs the master.
 */
public class Slave extends Node {

    private int localValue;
    private Constraint constraint;

    public Slave(String id, int initialValue, Constraint c, Channel com) {
        super(id,com);
        localValue = initialValue;
        constraint =  c;
    }

    @Override
    public void receiveMessage(Message msg) {

        Message reply = new Message(id, Message.MessageType.REPLY);

        // If the message is a get, then set the local sum as the body of the reply
        if(msg.getType().equals(Message.MessageType.GET)){
            reply.setBody(localValue);
            channel.send(Master.MASTER,reply);
        }

        // if the message is a new constraint, update the local constraint accordingly
	// TODO
    }

    // a new message is received in the stream
    public void update(int newValue){
        localValue += newValue;
        // if there is a constraint violation, inform the master
        if(constraint.violates(localValue)) {
            channel.send(
                    Master.MASTER,
                    new Message(id, Message.MessageType.CONSTRAINT_VIOLATION,localValue));
        }
    }

    // getters/setters

    public int getLocalValue() {
        return localValue;
    }

    public void setLocalValue(int localValue) {
        this.localValue = localValue;
    }

    public Constraint getConstraint() {
        return constraint;
    }

    public void setConstraint(Constraint constraint) {
        this.constraint = constraint;
    }


}
