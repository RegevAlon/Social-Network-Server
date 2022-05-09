package bgu.spl.net.impl.bgswise;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.BlockingConnectionHandler;
import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.NonBlockingConnectionHandler;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl<T> implements Connections<T> {
    int idCounter;
    ConcurrentHashMap<Integer, ConnectionHandler<T>> activeConnections;

    public ConnectionsImpl(){
        activeConnections = new ConcurrentHashMap<Integer, ConnectionHandler<T>>();
        idCounter = 0;
    }
    @Override
    public boolean send(int connectionId, T msg) {
        if (activeConnections.containsKey(connectionId)){
            activeConnections.get(connectionId).send(msg);
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void broadcast(T msg) {
        Collection<Integer> connections = activeConnections.keySet();
        for (int key : connections){
            activeConnections.get(key).send(msg);
        }
    }

    @Override
    public void disconnect(int connectionId) {
        activeConnections.remove(connectionId);

    }

    public void addBlockingConnection(BlockingConnectionHandler handler){
        handler.setId(idCounter);
        activeConnections.put(idCounter,handler);
        handler.getProtocol().start(idCounter,this);
        idCounter++;
    }

    public void addNonBlockingConnection(NonBlockingConnectionHandler handler){
        handler.setId(idCounter);
        activeConnections.put(idCounter,handler);
        handler.getProtocol().start(idCounter,this);
        idCounter++;
    }
}