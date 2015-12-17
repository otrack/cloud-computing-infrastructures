package eu.tsp.distsum;

import java.io.Serializable;

/**
 * A simple constrain class defining an upper and lower bound
 */
public class Constraint implements Serializable{

   private int lowBound;
   private int upperBound;

   public Constraint(int low, int high) {
      lowBound = low;
      upperBound = high;
   }

   public boolean violates(int value){
      if(value < lowBound
            || value > upperBound){
         return true;
      }
      return false;
   }

}
