package org.example.kvstore.cmd;

public class CommandFactory<K,V> {

    public Get<K,V> newGetCmd(K k){
        return new Get<>(k);
    }

    public Put<K,V> newPutCmd(K k, V v){
        return new Put<>(k, v);
    }

    public Reply<K,V> newReplyCmd(K k, V v){
        return new Reply<>(k, v);
    }

}
