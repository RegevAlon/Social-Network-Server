package bgu.spl.net.srv;


import bgu.spl.net.impl.bgswise.BidiMessagingProtocolImpl;
import bgu.spl.net.impl.bgswise.ConnectionsImpl;
import bgu.spl.net.impl.bgswise.MessageEncoderDecoderImpl;

import java.util.function.Supplier;

public class ThreadPerClientServer extends BaseServer {

    private ConnectionsImpl userConnections;

    public ThreadPerClientServer(
            int port,
            Supplier<BidiMessagingProtocolImpl> protocolFactory,
            Supplier<MessageEncoderDecoderImpl> encoderDecoderFactory) {

        super(port, protocolFactory, encoderDecoderFactory);
        this.userConnections = new ConnectionsImpl();

    }

    @Override
    protected void execute(BlockingConnectionHandler handler) {
        userConnections.addBlockingConnection(handler);
        new Thread(handler).start();
    }
}