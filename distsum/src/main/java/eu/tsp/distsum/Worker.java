package eu.tsp.distsum;

/**
 * The worker node tracks a stream of integers, and maintains a local sum of such values.
 * When an update violates its local constraint, the worker informs the coordinator.
 */
public class Worker extends Node {

    private int localValue;
    private Constraint constraint;

    public Worker(String id, int initialValue, Constraint c, Channel com) {
        super(id,com);
        localValue = initialValue;
        constraint =  c;
    }

    @Override
    public void receiveMessage(Message msg) {

        Message reply = new Message(id, Message.MessageType.REPLY);

        // If the message is a get then set the local sum as the body of the reply
        if(msg.getType().equals(Message.MessageType.GET)){
            reply.setBody(localValue);
            channel.sentTo(Coordinator.COORDINATOR,reply);
        }

        // TODO if the message is a new constraint update local constraint accordingly
        if(msg.getType().equals(Message.MessageType.CONSTRAINT_VIOLATION))
        {
            setConstraint((Constraint)msg.getBody());
            reply.setBody(localValue);
            channel.sentTo(Coordinator.COORDINATOR,reply);
        }
    }

    public boolean update(int newValue){
        localValue += newValue;
        if(constraint.violates(localValue)) {
            channel.sentTo(
                    Coordinator.COORDINATOR,
                    new Message(id, Message.MessageType.CONSTRAINT_VIOLATION,localValue));
            return true;
        }
        return false;
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
