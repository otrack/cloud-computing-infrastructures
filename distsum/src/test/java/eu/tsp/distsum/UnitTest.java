package eu.tsp.distsum;

import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.test.MultipleCacheManagersTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author  vagvaz
 * @author Pierre Sutra
 *
 * Created by vagvaz on 7/5/14.
 *
 * Scenario:
 *
 * There is a group of nodes that monitor some streams of updates. We want to compute the total sum of these updates.
 * One node from the group acts as a coordinator and the rest of the nodes as workers.
 * The coordinator maintains the global sum, on the other hand, the worker nodes listen to the streams
 * of updates and maintain a local sum. Furthermore, the worker nodes have some constrains, in our case
 * these constrains are two numbers an upper and a lower  bound. In case of an update violates the constrains,
 * then the worker informs the coordinator, which in turn asks all workers to
 * send their local values in order to recompute the global sum and the new constrains for each node.
 * After these recomputations, the coordinator sends the new constrains back to the workers.
 *
 * Scenario:
 * We have one coordinator and 3 worker nodes. We have 4 rounds. In each round we update each worker once. Each worker's update will be chosen
 * uniformly from the  following array   {1,1,1,1,2,2,-1,-1,-2}. At the end of each round we get the global sum perceived from the coordinator
 * Mind that the global sum might not be the actual sum, but it must always be inside the following values 0.9*realSum<= globalSum <= 1.1realSum.
 *
 * Using KVS/Infinispan as a communication channel:
 *
 * I tried to keep the code simple in order to demonstrate the communication needs and not to perplex things, by doing it
 * using infinispan listeners. Using infinispan we would have one cache that would generate the updates (updateCache).
 * On that cache we would have installed the worker listeners. The worker listeners whenever they would like to communication
 * with the coordinator, they would do a put operation on another cache (workerCache). The coordinator could be a clustered listener
 * installed on the workerCache, as a result, it listens to all the updates. What I cannot think is how through listeners the communication
 * from the coordinator to the workers can be achieved. I would not like to do updates to the updateCache. To sup up.
 * We have a clustered listener, the coordinator, installed on workerCache,
 * we have the worker listeners, worker nodes, installed locally to all the nodes containing keys of the updateCache
 * whenever worker nodes need to communicate with the coordinator do a put operation to the workerCache
 * whener the Coordinator wants to communicate with the workers ??
 *
 * Using the distributed executor could be a solution.
 *
 *
 */
@Test
public class UnitTest extends MultipleCacheManagersTest{

   private final int NUMBER_NODES = 1;

   public void run() {

      //The communication channel between coordinator and the workers
      Channel channel = new Channel(manager(0).<String, Message>getCache());
      Coordinator coord = new Coordinator(channel);

      //Create initial values and constrains for the workers
      int numOfWorkers = 10;
      ArrayList<Worker> workers = new ArrayList<Worker>(numOfWorkers);
      Map<String, Integer> workerValues = new HashMap<String, Integer>(numOfWorkers);
      Map<String, Constraint> workerConstrains = new HashMap<String, Constraint>(numOfWorkers);

      int initValue = 10;
      Constraint initConstraint = new Constraint(9, 11);
      for (int worker = 0; worker < numOfWorkers; worker++) {
         //Create new worker with initial values
         Worker w = new Worker(Integer.toString(worker), initValue, initConstraint, channel);
         workers.add(w);
         //put worker initial globalSum into the map
         workerValues.put(w.getId(), w.getLocalValue());
         //put worker's constrain into the map
         workerConstrains.put(w.getId(), w.getConstraint());
         //register worker to the channel
         channel.register(w.getId(), w);
      }

      //Initialize structures kept by coordinator
      coord.setLocalValues(workerValues);
      coord.setConstrains(workerConstrains);

      int[] updates = { 1, 1, 1, 1, 2, 2, -1, -1, -2 };
      int numberOfRounds = 4;
      Random rand = new Random();
      for (int round = 0; round < numberOfRounds; round++) {
         System.out.println("********* ROUND " + round + " ********");
         int realsum = 0;
         for (int worker = 0; worker < numOfWorkers; worker++) {
            int update = updates[rand.nextInt(updates.length)];
            int oldValue = workers.get(worker).getLocalValue();
            workers.get(worker).update(update);

            System.out.println(
                  "worker " + worker + ": update=" + update + " oldval=" + oldValue + " new newValue= " + workers
                        .get(worker).getLocalValue());
            realsum += workers.get(worker).getLocalValue();
         }

         try {
            Thread.sleep(1000);
         } catch (InterruptedException e) {
            e.printStackTrace();  // TODO: Customise this generated block
         }
         System.out.println("Real sum: " + realsum + " Global Sum: " + coord.getGlobalSum());
         System.out.println("********* END OF ROUND " + round + " ********\n\n");
         assert (0.5 * realsum <= coord.getGlobalSum());
         assert (1.5 * realsum >= coord.getGlobalSum());
      }

      try {
         Thread.sleep(1000);
      } catch (InterruptedException e) {
         e.printStackTrace();  // TODO: Customise this generated block
      }

   }

   @Override
   protected void createCacheManagers() throws Throwable {
      super.createCluster(getDefaultClusteredCacheConfig(CacheMode.DIST_SYNC),NUMBER_NODES);
   }
}

