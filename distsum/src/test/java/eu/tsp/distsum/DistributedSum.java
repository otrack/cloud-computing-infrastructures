package eu.tsp.distsum;

import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.test.MultipleCacheManagersTest;
import org.testng.annotations.Test;

@Test
public class DistributedSum extends MultipleCacheManagersTest{

    private static final int NUMBER_SLAVES = 1;

    public void baseCommunicationTest() {

    }
  
    public void distributedSumTest() {
      // TODO
    }

    @Override
    protected void createCacheManagers() throws Throwable {
        super.createCluster(getDefaultClusteredCacheConfig(CacheMode.REPL_SYNC), NUMBER_SLAVES+1);
    }
}

