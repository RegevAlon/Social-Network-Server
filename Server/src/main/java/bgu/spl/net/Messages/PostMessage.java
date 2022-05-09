package bgu.spl.net.Messages;

import bgu.spl.net.srv.DataBase;

public class PostMessage implements Message<ClientMessage> {
    private short opCode = 5;
    private String username;
    private String content;

    public PostMessage(String username, String content) {
        this.username = username;
        this.content = content;
    }

    public short getOpCode() {
        return opCode;
    }

    @Override
    public ClientMessage response() {
        DataBase db = DataBase.getInstance();
        if (db.IsLoggedIn(username)) {
            db.getUser(username).Post(content);
            return new AckMessage(opCode).response();
        }
        return new ErrorMessage(opCode).response();
    }
}