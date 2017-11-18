package eu.tsp.distsum;

import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;

/**
 * A node is an abstract object that has a identifier and listens to a channel.
 */

@Listener
public abstract class Node {

    protected String id;
    protected Channel channel;

    public Node(String i, Channel channel){
        this.id = i;
        this.channel = channel;
        this.channel.register(id,this);
    }

    @CacheEntryModified
    public void onCacheModification(CacheEntryModifiedEvent event){
        if (event.isPre()) return; // only consider post events
        Message m = (Message) event.getValue();
        this.receiveMessage(m);
    }

    public abstract void receiveMessage(Message msg);

    // getters/setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
