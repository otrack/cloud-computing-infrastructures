package org.example.abd.cmd;

import java.io.Serializable;

public abstract class Command<V> implements Serializable{

    int  tag;
    V v;

    public Command(V v, int tag){
        this.tag = tag;
        this.v = v;

    }

    public Integer getTag(){
        return tag;
    }

    public V getValue(){
        return v;
    }

}
