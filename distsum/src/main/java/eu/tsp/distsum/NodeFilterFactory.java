package eu.tsp.distsum;

import org.infinispan.metadata.Metadata;
import org.infinispan.notifications.cachelistener.filter.CacheEventFilter;
import org.infinispan.notifications.cachelistener.filter.CacheEventFilterFactory;
import org.infinispan.notifications.cachelistener.filter.EventType;

import java.io.Serializable;

public class NodeFilterFactory implements CacheEventFilterFactory {

   private static NodeFilterFactory instance;

   public static NodeFilterFactory getInstance(){
      if (instance==null) {
         instance = new NodeFilterFactory();
      }
      return instance;
   }

   private NodeFilterFactory() {}

   @Override
   public <K, V> CacheEventFilter<K, V> getFilter(final Object[] params) {
      assert params.length == 1;
      String id = (String) params[0];
      return new NodeFilter<>(id);
   }

   private static class NodeFilter<K,V> implements CacheEventFilter<K, V> , Serializable {

      private String identifier;

      private NodeFilter(String id){
         this.identifier = id;
      }

      @Override
      public boolean accept(K key, V oldValue, Metadata oldMetadata, V newValue, Metadata newMetadata,
            EventType eventType) {
         return identifier.equals(key);
      }
   }

}
