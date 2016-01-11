package eu.tsp.distsum;

import java.util.HashMap;
import java.util.Map;

/**
 * The coordinator that maintains the global sum
 * and computes the localConstraints given to the worker nodes.
 */
public class Coordinator extends Node{

   public static final String COORDINATOR = "COORDINATOR";

   private Map<String,Integer> localValues;   // the local values of all the worker nodes
   private Map<String,Constraint> constraintMap;  // the local constraints of all the worker nodes
   private int globalSum;                     // global sum

   private Channel channel;                // communication medium

   public Coordinator(Channel com) {
      super(COORDINATOR,com);
      localValues = new HashMap<>();
      constraintMap = new HashMap<>();
      this.channel = com;
   }

   /*
    * Receive a message from some worker.
    * This solely happens when there is a constraint violation at some worker node.
    */
   @Override
   public void receiveMessage(Message msg) {
      if (msg.getType().equals(Message.MessageType.REPLY)) {
         localValues.put(msg.getFrom(), (Integer) msg.getBody());
         // compute the new global sum
         recomputeValue();
         // compute the constraints
         computeConstrains();
         // send the constraints to the workers
         sendConstrains();
      } else if (msg.getType().equals(Message.MessageType.CONSTRAINT_VIOLATION)) {
         channel.broadCast(new Message(COORDINATOR, Message.MessageType.GET, null));
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
      recomputeValue();
   }

   public Map<String, Constraint> getConstrains() {
      return constraintMap;
   }

   public void setConstrains(Map<String, Constraint> constrains) {
      this.constraintMap = constrains;
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
   private void recomputeValue() {
      this.globalSum = 0;
      // iterate over local values and sum to compute the global sum globalSum
      for(Map.Entry<String,Integer> entry : localValues.entrySet()){
         this.globalSum += entry.getValue();
      }
   }

   /*
    * Compute the constrains.
    */
   private void computeConstrains() {
      // compute the drift each constrain will be equal to
      // localValue - drift...localValue + drift
      int drift = (int) Math.ceil(0.1*this.globalSum)/localValues.size();
      for(Map.Entry<String,Integer> entry : localValues.entrySet()){
         // get the current local globalSum;
         int localValue = entry.getValue();
         // store the new constraint
         constraintMap.put(entry.getKey(),new Constraint(localValue-drift,localValue+drift));
      }
   }

   /*
    * Send constraints back to the workers
    */
   private void sendConstrains() {
      // send the new constraint
      for (Map.Entry<String, Constraint> entry : constraintMap.entrySet()) {
         channel.sentTo(
                 entry.getKey(),
                 new Message(COORDINATOR, Message.MessageType.CONSTRAINT, entry.getValue()));
      }
   }

}
