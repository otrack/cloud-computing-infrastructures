package eu.tsp.transactions.base;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import eu.tsp.transactions.Bank;
import eu.tsp.transactions.Account;

public class BaseBank implements Bank{
  private Map<Integer,Account> accounts;

  public BaseBank(){
    this.accounts = new HashMap<>();
  }

  @Override
  public void createAccount(int id) throws IllegalArgumentException{
    if (this.accounts.containsKey(id)) {
      throw new IllegalArgumentException("account already existing: "+id);
    }
    accounts.put(id, new Account(id,0));
  }

  @Override
  public int getBalance(int id) throws IllegalArgumentException{
    if (!this.accounts.containsKey(id)) {
      throw new IllegalArgumentException("account not existing: "+id);
    }
    Account account = accounts.get(id);
    return account.getBalance();
  }

  @Override
  public void performTransfer(int from, int to, int amount){
    if (!this.accounts.containsKey(from)) {
      throw new IllegalArgumentException("account not existing: "+from);
    }
    
    if (!this.accounts.containsKey(to)) {
      throw new IllegalArgumentException("account not existing: "+to);
    }

    Account fromAccount = accounts.get(from);
    Account toAccount = accounts.get(to);
    
    fromAccount.setBalance(fromAccount.getBalance()-amount);
    toAccount.setBalance(toAccount.getBalance()+amount);
  }

  @Override
  public void clear(){
    this.accounts.clear();
  }
  
  @Override
  public void open(){}

  @Override
  public void close(){}
  
}
