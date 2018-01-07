package org.example.abd;

public interface Register<V> {

    V read();

    void write(V v);

}
