package eu.tsp.transactions.distributed;

import java.util.concurrent.ConcurrentMap;
import java.util.List;
import java.util.ArrayList;

import javax.transaction.TransactionManager;
import javax.transaction.RollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.xa.XAException;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.configuration.cache.ConfigurationBuilder;

import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.transaction.LockingMode;
import org.infinispan.transaction.TransactionMode;

import eu.tsp.transactions.Bank;
import eu.tsp.transactions.Account;

public class DistributedBank implements Bank{

  public DistributedBank(){
    // FIXME
  }

  @Override
  public void createAccount(int id) throws IllegalArgumentException{
    // FIXME
  }

  @Override
  public int getBalance(int id) throws IllegalArgumentException{
    // FIXME
    return 0;
  }

  @Override
  public void performTransfer(int from, int to, int amount){ 
    // FIXME
  }

  @Override
  public void clear(){
    // FIXME
  }
  
  @Override
  public void open(){
    // FIXME
  }

  @Override
  public void close(){
    // FIXME
  }
   
}
