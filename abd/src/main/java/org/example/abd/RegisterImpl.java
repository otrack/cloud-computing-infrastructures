package org.example.abd;

import org.example.abd.cmd.Command;
import org.example.abd.cmd.CommandFactory;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

public class RegisterImpl<V> extends ReceiverAdapter implements Register<V>{

    private String name;

    private CommandFactory<V> factory;
    private JChannel channel;

    public RegisterImpl(String name) {
        this.name = name;
        this.factory = new CommandFactory<>();
    }

    public void init(boolean isWritable) throws Exception{}

    @Override
    public void viewAccepted(View view) {}

    // Client part

    @Override
    public V read() {
        return null;
    }

    @Override
    public void write(V v) {}

    private V execute(Command cmd){
        return null;
    }

    // Message handlers

    @Override
    public void receive(Message msg) {}

    private void send(Address dst, Command command) {
        try {
            Message message = new Message(dst,channel.getAddress(), command);
            channel.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
