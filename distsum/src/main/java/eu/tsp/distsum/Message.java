package eu.tsp.distsum;

import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable {

   public static final Message EMPTYMSG = new Message("","");

   private String from;
   private String type;
   private Serializable body;
   private UUID id;

   public Message(String from,String type){
      this.from = from;
      this.type = type;
      this.id = UUID.randomUUID();
   }

   public Message(String from,String type,Serializable body){
      this.from = from;

      this.type =type;
      this.body = body;
   }

   // getters/setters

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public Object getBody() {
      return body;
   }

   public void setBody(Serializable body) {
      this.body = body;
   }

   public String getFrom() {
      return from;
   }

   public void setFrom(String from) {
      this.from = from;
   }

}
