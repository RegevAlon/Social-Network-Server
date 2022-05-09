package bgu.spl.net.srv;

import bgu.spl.net.impl.bgswise.BidiMessagingProtocolImpl;
import bgu.spl.net.impl.bgswise.MessageEncoderDecoderImpl;

public class reactorMain {
    public static void main(String[] args){
        new Reactor(10,
                7777,
                ()->new BidiMessagingProtocolImpl(),
                ()->new MessageEncoderDecoderImpl()).serve();
    }
}
