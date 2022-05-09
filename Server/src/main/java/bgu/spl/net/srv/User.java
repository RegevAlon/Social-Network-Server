package bgu.spl.net.srv;

import bgu.spl.net.Messages.ClientMessage;

import java.util.LinkedList;
import java.util.Vector;

public class User {
    private String username;
    private String password;
    private String birthday;
    private Vector<String> followList;
    private Vector<String> posts;
    private Vector<String> blockList;
    private LinkedList<ClientMessage> pending;
    private Vector<String> followers;
    private int id;
    public User(String username,String password,String birthday){
        this.username = username;
        this.password = password;
        this.birthday = birthday;
        posts = new Vector<>();
        followList = new Vector<>();
        blockList = new Vector<>();
        pending = new LinkedList<>();
        followers=new Vector<>();
    }

    public Vector<String> getBlockList() {
        return blockList;
    }

    public LinkedList<ClientMessage> getPending() {
        return pending;
    }

    public Vector<String> getPosts() {
        return posts;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getBirthday() {
        return birthday;
    }

    public Vector<String> getFollowList() {
        return followList;
    }
    public Vector<String> getFollowers() {
        return followers;
    }
    public void AddFollower(String follower) {followers.add(follower);}
    public void RemoveFollower(String follower) {followers.remove(follower);}
    public void ReceivePending(ClientMessage msg){pending.add(msg);}
    public boolean Follow(String user){
        if(followList.contains(user))
            return false;
        if(!blockList.contains(user)) {
            followList.add(user);
            DataBase db = DataBase.getInstance();
            db.getUser(user).AddFollower(username);
        }
        return true;
    }

    public boolean UnFollow(String user){
        if(!followList.contains(user))
            return false;
        followList.remove(user);
        DataBase db = DataBase.getInstance();
        db.getUser(user).RemoveFollower(username);
        return true;
    }
    public void Block(String username){blockList.add(username);}
    public void Post(String s){
        posts.add(s);
    }
    public boolean IsFollowing(String user){
        return followList.contains(user);
    }
    public boolean IsBlocked(String user){return blockList.contains(user);}
    public int getAge(){
        return 2022-Integer.parseInt(birthday.substring(6));
    }
}
