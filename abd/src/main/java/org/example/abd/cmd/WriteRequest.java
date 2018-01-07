package org.example.abd.cmd;

public class WriteRequest<V> extends Command<V> {

    public WriteRequest(V v, int tag){
        super(v, tag);
    }

    @Override
    public String toString() {
        return "WriteRequest{"+this.getValue()+","+this.getTag()+"}";
    }

}
