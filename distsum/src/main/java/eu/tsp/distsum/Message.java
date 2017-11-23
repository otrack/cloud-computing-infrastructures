package eu.tsp.distsum;

import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable {

   public static final Message EMPTYMSG = new Message("",MessageType.NULL);

   enum MessageType {
      NULL,
      REPLY,
      CONSTRAINT,
      CONSTRAINT_VIOLATION,
      GET
   }

   private String from;
   private MessageType type;
   private Serializable body;
   private UUID id;

   public Message(String from, MessageType type){
      this(from, type, null, UUID.randomUUID());
   }

   public Message(String from, MessageType type,Serializable body){
      this(from, type,body,UUID.randomUUID());
   }

   public Message(String from, MessageType type,Serializable body, UUID id){
      this.from = from;
      this.type = type;
      this.body = body;
      this.id = id;
   }

   // getters/setters

   public MessageType getType() {
      return type;
   }

   public void setType(MessageType type) {
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

   @Override
   public String toString(){
     return "["+from+","+id+","+body+","+type+"]";
   }

}
