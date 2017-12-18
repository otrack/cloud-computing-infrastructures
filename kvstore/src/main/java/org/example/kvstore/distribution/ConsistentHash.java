package org.example.kvstore.distribution;

import org.jgroups.Address;
import org.jgroups.View;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class ConsistentHash implements Strategy{

    private TreeSet<Integer> ring;
    private Map<Integer,Address> addresses;

    public ConsistentHash(View view){
    }

    @Override
    public Address lookup(Object key){
      return null;
    }

}
