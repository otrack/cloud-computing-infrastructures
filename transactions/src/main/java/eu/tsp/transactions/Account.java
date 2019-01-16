package eu.tsp.transactions;

import java.io.Serializable;
  
public class Account {
  private int id;
  private int balance;

  public Account(int id, int balance){
    this.id = id;;
    this.balance = balance;
  }

  public int getId(){
    return this.id;
  }

  public int getBalance(){
    return this.balance;
  }

  public void setBalance(int balance){
    this.balance = balance;    
  }
  
}
