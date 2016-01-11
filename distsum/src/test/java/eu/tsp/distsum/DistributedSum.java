package eu.tsp.distsum;

import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.test.MultipleCacheManagersTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Test
public class DistributedSum extends MultipleCacheManagersTest{

   private static final int NUMBER_NODES = 3;
   private static final int NUMBER_WORKERS = 10;

   public void distributedSumTest() {

      // create the communication channel between coordinator and the workers
      Channel channel = new Channel(manager(0).<String, Message>getCache());
      Coordinator coord = new Coordinator(channel);

      // create the initial values and constrains for workers
      ArrayList<Worker> workers = new ArrayList<>(NUMBER_WORKERS);
      Map<String, Integer> workerValues = new HashMap<>(NUMBER_WORKERS);
      Map<String, Constraint> workerConstrains = new HashMap<>(NUMBER_WORKERS);

      int initValue = 10;
      Constraint initConstraint = new Constraint(9, 11);
      for (int worker = 0; worker < NUMBER_WORKERS; worker++) {
         // create a new worker
         Worker w = new Worker(Integer.toString(worker), initValue, initConstraint, channel);
         workers.add(w);
         workerValues.put(w.getId(), w.getLocalValue());
         workerConstrains.put(w.getId(), w.getConstraint());
         // register worker to the channel
         channel.register(w);
      }

      // initialize the structures kept by the coordinator
      coord.setLocalValues(workerValues);
      coord.setConstrains(workerConstrains);

      int[] updates = { 1, 1, 1, 1, 2, 2, -1, -1, -2 };
      int numberOfRounds = 4;
      Random rand = new Random();
      for (int round = 0; round < numberOfRounds; round++) {
         System.out.println("********* ROUND " + round + " ********");
          int realSum = 0;
          for (int worker = 0; worker < NUMBER_WORKERS; worker++) {
              int update = updates[rand.nextInt(updates.length)];
              int oldValue = workers.get(worker).getLocalValue();
              workers.get(worker).update(update);

              System.out.println(
                      "worker " + worker + ": update=" + update + " oldValue=" + oldValue + " newValue=" + workers
                              .get(worker).getLocalValue());
              realSum += workers.get(worker).getLocalValue();
          }

          try {
              Thread.sleep(1000);
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
          System.out.println("Real sum: " + realSum + " Global Sum: " + coord.getGlobalSum());
          System.out.println("********* END OF ROUND " + round + " ********\n\n");
          assert (0.5 * realSum <= coord.getGlobalSum());
          assert (1.5 * realSum >= coord.getGlobalSum());
      }

       try {
           Thread.sleep(1000);
       } catch (InterruptedException e) {
           e.printStackTrace();
       }

   }

    @Override
    protected void createCacheManagers() throws Throwable {
        super.createCluster(getDefaultClusteredCacheConfig(CacheMode.DIST_SYNC),NUMBER_NODES);
    }
}

