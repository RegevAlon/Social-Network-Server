package bgu.spl.net.Messages;

import java.util.Vector;

public class ClientMessage{
    private short opCode;
    private Vector<Short> shorts;
    private String data;
    public ClientMessage(short _opCode, String _data){
        opCode= _opCode;
        data= _data;
        shorts = new Vector<>();
    }

    public ClientMessage(){
        data="";
        shorts = new Vector<>();

    }

    public void setData(String data) {
        this.data = data;
    }

    public void setOpCode(short opCode) {
        this.opCode = opCode;
    }

    public short getOpCode() {
        return opCode;
    }

    public String getData() {
        return data;
    }
    public void AddShort(short s){
        shorts.add(s);
    }

    public Vector<Short> getShorts() {
        return shorts;
    }
}