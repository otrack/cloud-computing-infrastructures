package eu.tsp.distsum;

/**
 * The worker node tracks updates on a stream
 * and maintains a local sum of the updates
 * when an update violates the its constrain,
 * the worker informs the coordinator.
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

      Message reply = new Message(id,"reply");

      // If the message is a get local values then set the local sum as body to the reply
      if(msg.getType().equals("get")){
         reply.setBody(localValue);
         channel.sentTo(Coordinator.COORDINATOR,reply);
      }

      // if the message is a new constraint just update the local constrain.
      else if (msg.getType().equals("constraint")){
         this.constraint = (Constraint) msg.getBody();
      }
   }

   public boolean update(int newvalue){
      localValue += newvalue;
      if(constraint.violates(localValue))
      {
         channel.sentTo(Coordinator.COORDINATOR, new Message(id,"violation",localValue));
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
