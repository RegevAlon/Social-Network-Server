package bgu.spl.net.Messages;

import bgu.spl.net.srv.DataBase;

public class FollowMessage implements Message<ClientMessage>{
    private short opCode = 4;
    private short follow;
    private String username;
    private String usernameToFollow;

    public FollowMessage(short follow,String username,String usernameToFollow){
        this.follow = follow;
        this.username=username;
        this.usernameToFollow = usernameToFollow;
    }
    public short getOpCode(){return opCode;}

    @Override
    public ClientMessage response() {
        DataBase db = DataBase.getInstance();
        if(db.IsRegistered(username)&&db.IsRegistered(usernameToFollow)) {
            if (db.IsLoggedIn(username)) {
                if (follow==0) {
                    if (!(db.getUser(username).IsBlocked(usernameToFollow) || db.getUser(usernameToFollow).IsBlocked(username))) {
                        if (db.getUser(username).Follow(usernameToFollow)) {
                            return new AckMessage(opCode, usernameToFollow).response();
                        }
                    }
                    else return new ErrorMessage(opCode).response();
                }
                else if(follow==1){
                    if (db.getUser(username).getFollowList().contains(usernameToFollow)) {
                        db.getUser(username).UnFollow(usernameToFollow);
                        return new AckMessage(opCode,usernameToFollow).response();
                    }
                }
            }
        }
        return new ErrorMessage(opCode).response();
    }
}
