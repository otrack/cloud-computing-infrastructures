package org.example.abd.cmd;

public class ReadReply<V> extends Command<V> {

    public ReadReply(V v, int label){
        super(v, label);
    }

    @Override
    public String toString() {
        return "ReadReply{"+this.getValue()+","+this.getLabel()+"}";
    }

}
