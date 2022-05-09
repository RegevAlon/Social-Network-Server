package bgu.spl.net.Messages;

import bgu.spl.net.srv.DataBase;

public class RegisterMessage implements Message<ClientMessage>{
    private short opCode = 1;
    private String username;
    private String password;
    private String birthday;

    public RegisterMessage(String username,String password,String birthday){
        this.username = username;
        this.password = password;
        this.birthday = birthday;
    }
    public short getOpCode(){return opCode;}

    @Override
    public ClientMessage response() {
        DataBase db = DataBase.getInstance();
        if(db.Register(username,password,birthday)) {
           return new AckMessage(opCode).response();
        }
        else return new ErrorMessage(opCode).response();
    }
}