package org.example.abd;

public class Manager {

    public static final String DEFAULT_STORE = "__reg";

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
            register.init(isWritable);
            return register;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
