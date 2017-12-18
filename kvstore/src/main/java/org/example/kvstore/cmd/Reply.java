package org.example.kvstore.cmd;

public class Reply<K,V> extends Command<K,V> {

    public Reply(K k, V v){
        super(k,v);
    }

    @Override
    public String toString() {
        return "Reply{"+this.getKey()+","+this.getValue()+"}";
    }


}
