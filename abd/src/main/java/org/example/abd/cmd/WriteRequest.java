package org.example.abd.cmd;

public class WriteRequest<V> extends Command<V> {

    public WriteRequest(V v, int label){
        super(v, label);
    }

    @Override
    public String toString() {
        return "WriteRequest{"+this.getValue()+","+this.getLabel()+"}";
    }

}
