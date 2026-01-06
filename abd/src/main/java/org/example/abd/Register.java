package org.example.abd;

public interface Register<V> {

    void open(boolean isWritable) throws Exception;

    V read();

    void write(V v);

    void close();

}
