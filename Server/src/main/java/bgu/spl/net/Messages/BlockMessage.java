package bgu.spl.net.Messages;

import bgu.spl.net.srv.DataBase;

public class BlockMessage implements Message<ClientMessage>{
    private short opcode = 12;
    private String username;
    private String usernameToBlock;
    public BlockMessage(String username,String usernameToBlock){
        this.username = username;
        this.usernameToBlock = usernameToBlock;
    }
    @Override
    public ClientMessage response() {
        DataBase db = DataBase.getInstance();
        if(db.IsRegistered(usernameToBlock)) {
            db.getUser(username).Block(usernameToBlock);
            if (db.getUser(username).IsFollowing(usernameToBlock))
                db.getUser(username).UnFollow(usernameToBlock);
            if (db.getUser(usernameToBlock).IsFollowing(username))
                db.getUser(usernameToBlock).UnFollow(username);
            return new AckMessage(opcode).response();
        }
        return new ErrorMessage(opcode).response();
    }

}
