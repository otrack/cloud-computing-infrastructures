package eu.tsp.distsum;

import org.infinispan.Cache;

/**
 *
 * A communication channel is a map of (String, Message) tuples,
 * where each entry is dedicated to a particular node.
 *
 * Sending a message m to a node N consists in executing
 * an asynchronous put(N,m) operation on the channel.
 *
 */
public class Channel {

   private Cache<String,Message> nodes;

   public Channel(Cache<String, Message> c) {
      nodes =  c;
   }

   /*
    * Add a node to the channel.
    */
   public void register(final String id, Node node){
      nodes.addListener(
              node,
              ComChannelFilterFactory.getInstance().getFilter(new Object[]{id}),
              null);
      nodes.put(id, Message.EMPTYMSG); // we create the entry
   }

   /*
    * Send a message to a node.
    */
   public void sentTo(String id, Message message){
      nodes.putAsync(id, message);
   }

   /*
    * Broadcast a message to all nodes, but the coordinator.
    */
   public void broadCast(Message message){
      for (String node: nodes.keySet()) {
         if (!node.equals(Coordinator.COORDINATOR)) {
            nodes.put(node,message);
         }
      }
   }

}
