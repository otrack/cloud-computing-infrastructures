package eu.tsp.transactions.base;

import eu.tsp.transactions.Bank;
import eu.tsp.transactions.BankFactory;
import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.assertEquals;

public class BaseBankTest{

  private static final int MAX_ACCOUNTS=1000;

  private Bank bank;
  
  @Before
  public void setup(){
    BankFactory factory = new BankFactory();
    this.bank = factory.createBaseBank();
    for (int i=0; i<MAX_ACCOUNTS; i++){
      bank.createAccount(i);
    }    
  }

  @Test(expected=IllegalArgumentException.class)
  public void getBalanceWrongAccount(){
    bank.getBalance(-1);
  }
  
  @Test
  public void simpleTransfer(){
    bank.performTransfer(1,2,100);
    assertEquals(bank.getBalance(1)+bank.getBalance(2),0);
  }

  
}
