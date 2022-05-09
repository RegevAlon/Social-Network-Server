package bgu.spl.net.Messages;

import bgu.spl.net.srv.User;
import bgu.spl.net.srv.DataBase;

public class LogStatMessage implements Message<ClientMessage> {
    private short opCode = 7;
    private String username;
    private String userInfo;

    public LogStatMessage(String username, String userInfo) {
        this.username = username;
        this.userInfo = userInfo;
    }

    public short getOpCode() {
        return opCode;
    }

    @Override
    public ClientMessage response() {
        DataBase db = DataBase.getInstance();
        ClientMessage output;
        User u = db.getUser(userInfo);
        if (u.IsBlocked(username))
            output = new ErrorMessage(opCode).response();
        else {
            output = new AckMessage(opCode).response();
            output.AddShort((short)u.getAge());
            output.AddShort((short)u.getPosts().size());
            output.AddShort((short)u.getFollowers().size());
            output.AddShort((short)u.getFollowList().size());
        }
        return output;
    }
}

