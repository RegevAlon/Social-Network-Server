package bgu.spl.net.Messages;

import bgu.spl.net.srv.User;
import bgu.spl.net.srv.DataBase;

public class StatsMessage implements Message<ClientMessage>{
    private short opCode = 8;
    private String userInfo;
    private String username;
    public StatsMessage(String username,String userInfo){
        this.username = username;
        this.userInfo = userInfo;
    }
    public short getOpCode(){return opCode;}
    @Override
    public ClientMessage response() {
        DataBase db = DataBase.getInstance();
        ClientMessage output;
        String msg="";
        User user = db.getUser(userInfo);
        if(user.IsBlocked(username))
            return new ErrorMessage(opCode).response();
        else {
            output = new AckMessage(opCode).response();
            output.AddShort((short)user.getAge());
            output.AddShort((short)user.getPosts().size());
            output.AddShort((short)user.getFollowers().size());
            output.AddShort((short)user.getFollowList().size());
            return output;
        }
    }
}
