package bgu.spl.net.srv;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class DataBase {
    private ConcurrentHashMap<String, User> registers;
    private ConcurrentHashMap<Integer,String> logins;
    private Vector<String> filters;

    private static class DataBaseHolder{
        private static DataBase instance=new DataBase();
    }
    public static DataBase getInstance() {
        return DataBaseHolder.instance;
    }

    private DataBase(){
        registers = new ConcurrentHashMap<>();
        logins = new ConcurrentHashMap<>();
        filters = new Vector<>();
	filters.add("hitler");
	filters.add("war");
	filters.add("trump");
	filters.add("gigi");
    }
    public Vector<String> getFilters(){return filters;}
    public ConcurrentHashMap<Integer,String> getLogins() {return logins;}
    public ConcurrentHashMap<String, User> getRegisters() {return registers;}
    public boolean IsRegistered(String username){
        return registers.containsKey(username);
    }
    public boolean IsLoggedIn(String username){return logins.containsValue(username);}
    public boolean Register(String username,String pass,String birthday){
        if(!registers.containsKey(username)) {
            User new_user = new User(username,pass,birthday);
            registers.put(username,new_user);
            return true;
        }
        return false;
    }
    public boolean Login(String username, String password) {
        if(IsRegistered(username))
            if (!IsLoggedIn(username))
                if(registers.get(username).getPassword().equals(password)) {
                    return true;
                }
        return false;
    }
    public void Logout(String username){
        logins.remove(getUser(username).getId());
        getUser(username).setId(-1);
    }
    public User getUser(String user){return registers.get(user);}
    public void setFilters(String word){
        filters.add(word);
    }
}
