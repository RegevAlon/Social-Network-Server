package bgu.spl.net.Messages;

import bgu.spl.net.srv.DataBase;

public class PMMessage implements Message<ClientMessage>{
    private short opCode = 6;
    private String username;
    private String usernameToSend;
    private String content;
    private String date;

    public PMMessage(String username,String usernameToSend,String content){
        this.username = username;
        this.usernameToSend = usernameToSend;
        this.content = content;
    }
    public short getOpCode(){return opCode;}

    @Override
    public ClientMessage response() {
        DataBase db = DataBase.getInstance();
        if(db.IsLoggedIn(username)) {
            if (db.IsRegistered(usernameToSend)) {
                if (db.getUser(usernameToSend).IsFollowing(username)) {
                    if (!(db.getUser(usernameToSend).IsBlocked(username) && db.getUser(username).IsBlocked(username))) {
                        return new AckMessage(opCode).response();
                    }
                }
            }
        }
        return new ErrorMessage(opCode).response();
    }
}
