package org.example.kvstore.cmd;

public class Put<K,V> extends Command<K,V> {

    public Put(K k, V v){
        super(k,v);
    }


    @Override
    public String toString() {
        return "Put{"+this.getKey()+","+this.getValue()+"}";
    }

}
