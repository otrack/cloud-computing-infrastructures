package org.example.abd.cmd;

import java.io.Serializable;

public abstract class Command<V> implements Serializable{

    int label;
    V v;

    public Command(V v, int label){
        this.label = label;
        this.v = v;

    }

    public Integer getLabel(){
        return label;
    }

    public V getValue(){
        return v;
    }

}
