package eu.tsp.transactions;

public class Account {

    int id;
    int balance;
    String countryCode;

    // Constructor with country code
    public Account(int id, int balance, String countryCode) {
        this.id = id;
        this.balance = balance;
        this.countryCode = countryCode;
    }

    // Constructor with default country code
    public Account(int id, int balance) {
        this(id, balance, "FR");
    }

    public int getId() {
        return id;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
