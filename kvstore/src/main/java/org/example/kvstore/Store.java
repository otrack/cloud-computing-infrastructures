package org.example.kvstore;

public interface Store<K,V> {

    void open() throws Exception;
    
    V get(K k);

    V put(K k, V v);

    void close();
    
}
