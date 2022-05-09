package bgu.spl.net.Messages;

import bgu.spl.net.srv.DataBase;

public class LoginMessage implements Message<ClientMessage> {
    private short opCode = 2;
    private String username;
    private String password;
    private short captcha;

    public LoginMessage(String username, String password, short captcha) {
        this.username = username;
        this.password = password;
        this.captcha = captcha;
    }
    public short getOpCode(){return opCode;}

    @Override
    public ClientMessage response() {
        DataBase db = DataBase.getInstance();
        if (captcha == 0)
            return new ErrorMessage(opCode).response();
        if (db.Login(username, password))
            return new AckMessage(opCode).response();
        return new ErrorMessage(opCode).response();
    }
}
