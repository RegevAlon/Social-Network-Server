package bgu.spl.net.Messages;

import bgu.spl.net.srv.DataBase;

public class LogoutMessage implements Message<ClientMessage>{
    private short opCode = 3;
    public String username;
    public LogoutMessage(String username){
        this.username = username;
    }
    public short getOpCode(){return opCode;}

    @Override
    public ClientMessage response() {
        DataBase db = DataBase.getInstance();
        if(db.IsLoggedIn(username)){
            db.Logout(username);
            return new AckMessage(opCode).response();
        }
        return new ErrorMessage(opCode).response();
    }
}
