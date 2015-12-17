package eu.tsp.distsum;

import java.util.HashMap;
import java.util.Map;

/**
 * The coordinator that maintains the global sum
 * and computes the localConstrains given to the worker nodes.
 */
public class Coordinator extends Node{

   public static final String COORDINATOR = "COORDINATOR";

   private Map<String,Integer> localValues;   //the local values of all worker nodes
   private Map<String,Constraint> constrains;  //the local constrains of all worker nodes
   private int globalSum;                     //global sum
   private Channel channel;                //communication medium

   public Coordinator(Channel com) {
      super(COORDINATOR,com);
      localValues = new HashMap<>();
      constrains = new HashMap<>();
      this.channel = com;
   }

   /*
    * Receive message from worker.
    * This is called only when there is a violation on a worker node.
    */
   @Override
   public void receiveMessage(Message msg) {
      if (msg.getType().equals("reply")) {
         localValues.put(msg.getFrom(), (Integer) msg.getBody());
         //Compute new global sum
         recomputeValue();
         //Compute constrains
         computeConstrains();
         //Send Constrains to workers
         sendConstrains();
      } else if (msg.getType().equals("violation")) {
         channel.broadCast(new Message(COORDINATOR, "get", null));
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
      return constrains;
   }

   public void setConstrains(Map<String, Constraint> constrains) {
      this.constrains = constrains;
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
      //Iterate over local values and sum to compute the global sum globalSum
      for(Map.Entry<String,Integer> entry : localValues.entrySet()){
         this.globalSum += entry.getValue();
      }
   }

   /*
    * Compute the constrains.
    */
   private void computeConstrains() {
      // Compute the drift each constrain will be equal to
      // localValue - drift...localValue + drift
      int drift = (int) Math.ceil(0.1*this.globalSum)/localValues.size();
      for(Map.Entry<String,Integer> entry : localValues.entrySet()){

         //get current local globalSum;
         int localValue = entry.getValue();
         //put new constrain to map
         constrains.put(entry.getKey(),new Constraint(localValue-drift,localValue+drift));
      }
   }

   /*
    * Send constrains back to workers.
    */
   private void sendConstrains() {
      //Send to each worker node the new constraint
      for (Map.Entry<String, Constraint> entry : constrains.entrySet()) {
         channel.sentTo(
               entry.getKey(),
               new Message(COORDINATOR, "constraint", entry.getValue()));
      }
   }

}
