package eu.tsp.distsum;

import org.infinispan.metadata.Metadata;
import org.infinispan.notifications.cachelistener.filter.CacheEventFilter;
import org.infinispan.notifications.cachelistener.filter.CacheEventFilterFactory;
import org.infinispan.notifications.cachelistener.filter.EventType;

import java.io.Serializable;

public class ComChannelFilterFactory implements CacheEventFilterFactory {

   private static ComChannelFilterFactory instance;

   public static ComChannelFilterFactory getInstance(){
      if (instance==null) {
         instance = new ComChannelFilterFactory();
      }
      return instance;
   }

   private ComChannelFilterFactory() {}

   @Override
   public <K, V> CacheEventFilter<K, V> getFilter(final Object[] params) {
      assert params.length == 1;
      String id = (String) params[0];
      return new ComChannelCacheEventFilter<>(id);
   }

   private static class ComChannelCacheEventFilter<K,V> implements CacheEventFilter<K, V> , Serializable {

      private String identifier;

      private ComChannelCacheEventFilter(String id){
         this.identifier = id;
      }

      @Override
      public boolean accept(K key, V oldValue, Metadata oldMetadata, V newValue, Metadata newMetadata,
            EventType eventType) {
         return identifier.equals(key);
      }
   }

}
