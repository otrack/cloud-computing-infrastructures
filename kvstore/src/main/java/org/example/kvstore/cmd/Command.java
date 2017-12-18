package org.example.kvstore.cmd;

import java.io.Serializable;

public abstract class Command<K,V> implements Serializable{

    K k;
    V v;

    public Command(K k, V v){
        this.k = k;
        this.v = v;
    }

    public K getKey(){
        return k;
    }

    public V getValue(){
        return v;
    }

}
