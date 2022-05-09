package bgu.spl.net.Messages;

public class ErrorMessage implements Message<ClientMessage>{
    private short opCode=11;
    private short mOpCode;
    public ErrorMessage(short mOpCode){
        this.mOpCode=mOpCode;
    }

    @Override
    public ClientMessage response() {
        ClientMessage output = new ClientMessage();
        output.setOpCode(opCode);
        output.AddShort(mOpCode);
        return output;
    }
    public short getOpCode(){return opCode;}

}
