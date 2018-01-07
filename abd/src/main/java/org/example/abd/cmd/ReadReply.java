package org.example.abd.cmd;

public class ReadReply<V> extends Command<V> {

    public ReadReply(V v, int tag){
        super(v, tag);
    }

    @Override
    public String toString() {
        return "ReadReply{"+this.getValue()+","+this.getTag()+"}";
    }

}
