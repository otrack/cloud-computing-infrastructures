package org.example.kvstore;

public interface Store<K,V> {

    V get(K k);

    V put(K k, V v);

}
