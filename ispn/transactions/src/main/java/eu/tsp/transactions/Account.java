package eu.tsp.transactions;

public class Account {

    int id;

    int balance;

    public Account(int id, int balance) {
        this.id = id;
        this.balance = balance;
    }

    public Account() {}

    public int getId() {
        return id;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
