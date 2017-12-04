package eu.tsp.distsum;

import java.io.Serializable;

/**
 * A simple constraint class defining an upper and lower bound
 */
public class Constraint implements Serializable{

   private int lowerBound;
   private int upperBound;

   public Constraint(int low, int high) {
      lowerBound = low;
      upperBound = high;
   }

   public boolean violates(int value){
      if(value < lowerBound
              || value > upperBound){
         return true;
      }
      return false;
   }

}
