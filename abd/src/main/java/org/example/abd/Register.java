package org.example.abd;

public interface Register<V> {

    void open();

    V read();

    void write(V v);

    void close();

}
