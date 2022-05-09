package bgu.spl.net.srv;

import bgu.spl.net.impl.bgswise.BidiMessagingProtocolImpl;
import bgu.spl.net.impl.bgswise.MessageEncoderDecoderImpl;

public class TPCmain {

    public static void main(String[] args) {
        new ThreadPerClientServer(7777,
                ()->new BidiMessagingProtocolImpl(),
                ()->new MessageEncoderDecoderImpl()).serve();
    }
}
