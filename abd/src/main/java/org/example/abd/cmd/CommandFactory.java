package org.example.abd.cmd;

public class CommandFactory<V> {

    public ReadRequest<V> newReadRequest(){
        return new ReadRequest();
    }

    public ReadReply<V> newReadReply(V v, int tag){
        return new ReadReply(v, tag);
    }

    public WriteRequest<V> newWriteRequest(V v, int tag){
        return new WriteRequest<>(v, tag);
    }

    public WriteReply newWriteReply(){
        return new WriteReply();
    }

}
