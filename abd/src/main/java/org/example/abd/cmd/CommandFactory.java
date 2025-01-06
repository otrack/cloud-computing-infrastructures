package org.example.abd.cmd;

public class CommandFactory<V> {

    public ReadRequest<V> newReadRequest(){
        return new ReadRequest();
    }

    public ReadReply<V> newReadReply(V v, int label){
        return new ReadReply(v, label);
    }

    public WriteRequest<V> newWriteRequest(V v, int label){
        return new WriteRequest<>(v, label);
    }

    public WriteReply newWriteReply(){
        return new WriteReply();
    }

}
