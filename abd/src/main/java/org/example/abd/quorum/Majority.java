package org.example.abd.quorum;

import org.jgroups.Address;
import org.jgroups.View;

import java.util.List;

public class Majority {

    public Majority(View view){}

    public int quorumSize(){
        return 0;
    }

    public List<Address> pickQuorum(){
        return null;
    }

}
