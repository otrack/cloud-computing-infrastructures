package org.example.abd;

import java.util.ArrayList;
import java.util.List;

public class Manager {

    public static final String DEFAULT_STORE = "__reg";

    private List<Register> registers = new ArrayList<Register>();

    public <V> Register<V> newRegister() {
        return newRegister(DEFAULT_STORE);
    }

    public <V> Register<V> newRegister(String name){
        return newRegister(name, false);
    }

    public <V> Register<V> newRegister(boolean isWritable){
        return newRegister(DEFAULT_STORE, isWritable);
    }

    public <V> Register<V> newRegister(String name, boolean isWritable){
        try {
            RegisterImpl<V> register = new RegisterImpl(name);
            register.open(isWritable);
            return register;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void stop(){
        for(Register register: registers) {
            register.close();
        }
    }

}
