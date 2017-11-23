package eu.tsp.distsum;

import java.util.HashMap;
import java.util.Map;

/**
 * The master maintains the global sum.
 * It also computes the constraints given to the slaves.
 */
public class Master extends Node{

   public static final String MASTER = "MASTER";

   private Map<String,Integer> localValues;   // the local values of all the worker nodes
   private Map<String,Constraint> constraintMap;  // the local constraints of all the worker nodes
   private int globalSum;                     // the (approximated) global sum

   public Master(Channel com) {
      super(MASTER,com);
      localValues = new HashMap<>();
      constraintMap = new HashMap<>();
   }

   /*
    * Receive a message from some worker.
    * This solely happens when there is a constraint violation at that node.
    */
   @Override
   public void receiveMessage(Message msg) {
      if (msg.getType().equals(Message.MessageType.REPLY)) {
         localValues.put(msg.getFrom(), (Integer) msg.getBody());
         // compute the new global sum
         recomputeGlobalSum();
         // compute the constraints
         computeConstraints();
         // send the constraints to the workers
         sendConstraints();
      } else if (msg.getType().equals(Message.MessageType.CONSTRAINT_VIOLATION)) {
         channel.broadcast(new Message(MASTER, Message.MessageType.GET, null));
      } else {
         throw new RuntimeException("Invalid message");
      }
   }

   // setters/getters

   public Map<String, Integer> getLocalValues() {
      return localValues;
   }

   public void setLocalValues(Map<String, Integer> localValues) {
      this.localValues = localValues;
      recomputeGlobalSum();
   }

   public Map<String, Constraint> getConstraints() {
      return constraintMap;
   }

   public void setConstraints(Map<String, Constraint> constraints) {
      this.constraintMap = constraints;
   }

   public int getGlobalSum() {
      return globalSum;
   }

   public void setGlobalSum(int globalSum) {
      this.globalSum = globalSum;
   }


   // helpers

   /*
    * Recompute the global sum.
    */
   private void recomputeGlobalSum() {
      this.globalSum = 0;
      // iterate over all the local values and sum them
      for(Map.Entry<String,Integer> entry : localValues.entrySet()){
         this.globalSum += entry.getValue();
      }
   }

   /*
    * Compute the constraints.
    */
   private void computeConstraints() {
      int drift = (int) Math.ceil(0.1*this.globalSum)/localValues.size();
      for(Map.Entry<String,Integer> entry : localValues.entrySet()){
         // get the current local globalSum;
         int localValue = entry.getValue();
         // store the new constraint
         constraintMap.put(entry.getKey(),
                 new Constraint(localValue-drift,localValue+drift));
      }
   }

   /*
    * Send constraints back to the workers
    */
   private void sendConstraints() {
     // TODO
   }

}
