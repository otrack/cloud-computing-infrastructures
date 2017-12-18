package org.example.kvstore.cmd;

public class Get<K,V> extends Command<K,V> {

    public Get(K k){
        super(k,null);
    }

    @Override
    public String toString() {
        return "Get{"+this.getKey()+"}";
    }


}
