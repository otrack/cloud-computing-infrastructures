package org.example.kvstore;

import org.example.kvstore.cmd.CommandFactory;
import org.example.kvstore.distribution.Strategy;

import java.util.Map;

public class StoreImpl<K,V> implements Store<K,V>{

    private String name;
    private Strategy strategy;
    private Map<K,V> data;
    private CommandFactory<K,V> factory;

    public StoreImpl(String name) {
        this.name = name;
    }

    @Override
    public void open() throws Exception{
    }

    @Override
    public V get(K k) {
      return null;
    }

    @Override
    public V put(K k, V v) {
      return null;
    }

    @Override
    public String toString(){
        return "Store#"+name+"{"+data.toString()+"}";
    }

    @Override
    public void close(){
    }

}
