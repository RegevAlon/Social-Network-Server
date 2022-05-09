package bgu.spl.net.Messages;

public class AckMessage implements Message<ClientMessage>{
    private short opCode=10;
    private short msgOpCode;
    private String optional="";
    public AckMessage(short msgOpCode){
        this.msgOpCode = msgOpCode;
    }
    public AckMessage(short msgOpCode,String optional)
    {
        this.msgOpCode = msgOpCode;
        this.optional = optional;
    }

    @Override
    public ClientMessage response() {
        ClientMessage output = new ClientMessage();
        output.setOpCode(opCode);
        output.AddShort(msgOpCode);
        output.setData(optional);
        return output;
    }
    public short getOpCode(){return opCode;}
}
