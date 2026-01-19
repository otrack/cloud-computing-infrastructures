package eu.tsp.transactions;

import sun.misc.Signal;
import sun.misc.SignalHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.port;
import static spark.Spark.init;
import static spark.Spark.stop;
import static spark.Spark.get;
import static spark.Spark.put;
import static spark.Spark.post;
import static spark.Spark.before;

public class Server {

  final static Logger LOG = LoggerFactory.getLogger(Server.class);
  
  public static void main(String[] args) {

    final BankFactory factory = new BankFactory();
    final Bank bank = factory.createBank();
    bank.open();
    
    port(8080);
        
    get("/:id", (req, res) -> {
	int id = Integer.parseInt(req.params("id"));
	LOG.info("getBalance("+id+")");
	return Integer.toString(bank.getBalance(id));
      });
    
    post("/:id", (req, res) -> {
	int id = Integer.parseInt(req.params("id"));
	LOG.info("createAccount("+id+")");
	bank.createAccount(id);
	return "OK";
      });    
    
    post("/clear/all", (req, res) -> {
	LOG.info("clear()");
 	bank.clear();
	return "OK";
      });
    
    SignalHandler sh = new SignalHandler() {
	@Override
	public void handle(Signal s) {
	  LOG.info("Shutting down ..");
	  bank.close();
	  System.exit(0);
	  stop();
	}
      };

    Signal.handle(new Signal("INT"), sh);
    Signal.handle(new Signal("TERM"), sh);

    Thread.currentThread().interrupt();
  }

}
