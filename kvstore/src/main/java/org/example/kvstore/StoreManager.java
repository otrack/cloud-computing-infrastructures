package org.example.kvstore;

import java.util.ArrayList;
import java.util.List;

public class StoreManager {

    List<Store> stores = new ArrayList<>();

    public static final String DEFAULT_STORE = "__kvstore";

    public <K,V> Store<K,V> newStore() {
        return newStore(DEFAULT_STORE);
    }

   public <K,V> Store newStore(String name){
        try {
            StoreImpl<K,V> store = new StoreImpl(name);
            store.open();
            stores.add(store);
            return store;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void stop(){
        for(Store store: stores) {
            store.close();
        }
    }

}
