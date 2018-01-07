package org.example.abd.cmd;

public class ReadRequest<V> extends Command<V> {

    public ReadRequest(){
        super(null,0);
    }

    @Override
    public String toString() {
        return "ReadRequest{"+this.getValue()+"}";
    }


}
