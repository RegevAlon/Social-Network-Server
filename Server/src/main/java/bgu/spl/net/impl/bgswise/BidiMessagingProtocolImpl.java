package bgu.spl.net.impl.bgswise;

import bgu.spl.net.Messages.*;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.DataBase;
import bgu.spl.net.srv.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<ClientMessage>{
    private boolean shouldTerminate = false;
    public int connectionId;
    Connections activeConnections;

    /**
     * Used to initiate the current client protocol with it's personal connection ID and the connections implementation
     **/
    @Override
    public void start(int connectionId, Connections connections){
        this.connectionId = connectionId;
        this.activeConnections = connections;
    }

    @Override
    public void process(ClientMessage msg) {
        ClientMessage output;
        DataBase db=DataBase.getInstance();
        String[] input = msg.getData().split(",");
        switch (msg.getOpCode()) {
            case 1: {
                output = new RegisterMessage(input[0],input[1],input[2]).response();
                activeConnections.send(connectionId,output);
                break;
            }
            case 2: {
                output=new LoginMessage(input[0],input[1],msg.getShorts().get(0)).response();
                //db.getUser(input[1]).setId(connectionId);
                activeConnections.send(connectionId,output);
                if(output.getOpCode()==(short) 10) {
                    User user = db.getUser(input[0]);
                    user.setId(connectionId);
                    db.getLogins().put(connectionId,user.getUsername());
                    while(!user.getPending().isEmpty()){
                        activeConnections.send(connectionId,user.getPending().poll());
                    }
                }
                break;
            }
            case 3: {
                String username = db.getLogins().get(connectionId);
                if(username!=null)
                    output=new LogoutMessage(username).response();
                else output = new ErrorMessage((short)3).response();
                activeConnections.send(connectionId,output);
                break;
            }
            case 4: {
                if(db.getLogins().containsKey(connectionId)) {
                    String username = db.getLogins().get(connectionId);
                    output = new FollowMessage(msg.getShorts().get(0),username, input[0]).response();
                }
                else output=new ErrorMessage((short)4).response();
                activeConnections.send(connectionId,output);
                break;
            }
            case 5: {
                if (IsLoggedIn()) {
                    String username = db.getLogins().get(connectionId);
                    String s = "";
                    for (int i = 0; i < input.length; i++)
                        s += input[i];
                    output = new PostMessage(username, s).response();
                    if(output.getOpCode()==(short)10) {
                        User user = db.getUser(db.getLogins().get(connectionId));
                        for (int i = 0; i < user.getFollowers().size(); i++) {
                            if (db.IsLoggedIn(user.getFollowers().get(i))) {
                                activeConnections.send(db.getUser(user.getFollowers().get(i)).getId(),
                                        new NotificationMessage(user.getFollowers().get(i),(short)1,username,s).response());
                            }
                            else {
                                db.getUser(user.getFollowers().get(i)).ReceivePending(new NotificationMessage(user.getFollowers().get(i),
                                        (short)1,username,s).response());
                            }
                        }
                        Vector<String> tags = getTags(s);
                        for(int i=0;i<tags.size();i++){
                            if(db.IsRegistered(tags.get(i))){
                                if(db.getUser(tags.get(i)).IsBlocked(username)||db.getUser(username).IsBlocked(tags.get(i)))
                                    continue;
                                else {
                                    if(db.IsLoggedIn(tags.get(i)))
                                        activeConnections.send(db.getUser(tags.get(i)).getId(),new NotificationMessage(tags.get(i),
                                                (short)1,username,s).response());
                                    else  db.getUser(tags.get(i)).ReceivePending(new NotificationMessage(tags.get(i),
                                            (short)1,username,s).response());
                                }
                            }
                        }
                    }
                }
                else output = new ErrorMessage((short) 5).response();
                activeConnections.send(connectionId, output);
                break;
            }
            case 6: {
                String date;
                String username = db.getLogins().get(connectionId);
                SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date now = new Date();
                date = sdfDate.format(now);
                output = new PMMessage(username,input[0],input[1]).response();
                if(output.getOpCode()==(short)10){
                    if(db.IsLoggedIn(input[0])){
                        ClientMessage notification = new NotificationMessage(input[0],(short)0,username,filter(input[1]+" "+date,db.getFilters())).response();
                        activeConnections.send(db.getUser(input[0]).getId(),notification);
                    }
                    else {
                        db.getUser(input[0]).ReceivePending(new NotificationMessage(input[0],(short)0,username,filter(input[1]+" "+date,db.getFilters())).response());
                    }
                }
                activeConnections.send(connectionId,output);
                break;
            }
            case 7: {
                if(IsLoggedIn()){
                    String user = db.getLogins().get(connectionId);
                    for(User u:db.getRegisters().values()){
                        output = new LogStatMessage(user,u.getUsername()).response();
                        activeConnections.send(connectionId,output);
                    }
                }
                else {
                    output = new ErrorMessage(msg.getOpCode()).response();
                    activeConnections.send(connectionId,output);
                }
                break;
            }
            case 8: {
                if(IsLoggedIn()) {
                    String user = db.getLogins().get(connectionId);
                    String[] userList = input[0].split("\\|");
                    for (String u:userList){
                        if(db.IsRegistered(u)){
                            output = new StatsMessage(user,u).response();
                            activeConnections.send(connectionId,output);
                        }
                        else activeConnections.send(connectionId,new ErrorMessage(msg.getOpCode()).response());
                    }
                }
                else {
                    output = new ErrorMessage(msg.getOpCode()).response();
                    activeConnections.send(connectionId,output);
                }
                break;
            }
            case 9: {
                if (IsLoggedIn()) {
                    String user = db.getLogins().get(connectionId);
                    output = new NotificationMessage(user,(short)1, input[1], input[2]).response();
                    activeConnections.send(connectionId, output);
                    break;
                }
                else activeConnections.send(connectionId,new ErrorMessage(msg.getOpCode()).response());
            }
            case 10: {
                if(!input[1].equals(""))
                    output=new AckMessage(msg.getShorts().get(0),input[1]).response();
                else output=new AckMessage(msg.getShorts().get(0)).response();
                activeConnections.send(connectionId,output);
                break;
            }
            case 11: {
                output=new ErrorMessage(msg.getShorts().get(0)).response();
                activeConnections.send(connectionId,output);
                break;
            }
            case 12: {
                String user = db.getLogins().get(connectionId);
                output=new BlockMessage(user,input[0]).response();
                activeConnections.send(connectionId,output);
                break;
            }
        }
    }
    private boolean IsLoggedIn() {
        DataBase db = DataBase.getInstance();
        return db.getLogins().containsKey(connectionId);
    }
    private boolean isBlank(String s){
        if(s.isEmpty())
            return true;
        else {
            for(char c: s.toCharArray()){
                if(c!=' ')
                    return false;
            }
        }
        return true;
    }
    private String filter(String s,Vector<String> filter) {
        String filteredMsg = "";
        String[] words = s.split(" ");
        for(int i=0;i<words.length;i++){
            if (filter.contains(words[i].toLowerCase())) {
                filteredMsg += "<filtered> ";
            }
            else {
                filteredMsg+=words[i]+" ";
            }
        }
        return filteredMsg.substring(0,filteredMsg.length()-1);
    }
    private Vector<String> getTags(String s){
        Vector<String> tags = new Vector<>();
        if(s.contains("@")) {
            s=s.substring(s.indexOf("@")+1);
            String[] t = s.split("@");
            for (int i = 0; i < t.length; i++) {
                if (t[i].contains(" ")) {
                    tags.add(t[i].substring(0, t[i].indexOf(" ")));
                } else tags.add(t[i]);
            }
        }
        return tags;
    }

    public boolean shouldTerminate(){
        return shouldTerminate;
    }

}
