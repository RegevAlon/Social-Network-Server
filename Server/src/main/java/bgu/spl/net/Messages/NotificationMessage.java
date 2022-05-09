package bgu.spl.net.Messages;

public class NotificationMessage implements Message<ClientMessage> {
    private short opCode = 9;
    private String username;
    private short notificationType;
    private String postingUser;
    String content;

    public NotificationMessage(String username, short notificationType, String postingUser, String content) {
        this.username = username;
        this.notificationType = notificationType;
        this.postingUser = postingUser;
        this.content = content;
    }

    @Override
    public ClientMessage response() {
        ClientMessage output = new ClientMessage();
        output.setOpCode(opCode);
        output.getShorts().add(notificationType);
        output.setData(postingUser+";"+content);
        return output;
    }
    public short getOpCode(){return opCode;}
}