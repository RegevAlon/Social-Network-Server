package bgu.spl.net.impl.bgswise;

import bgu.spl.net.Messages.ClientMessage;
import bgu.spl.net.api.MessageEncoderDecoder;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<ClientMessage> {

    private byte[] bytes;
    private int len;
    private int zeroCounter;
    private int byteCounter;
    private int shortCounter;
    private byte[] opCodeBytes;
    private byte[] shortCodeBytes;
    private short msgOpCode;
    private boolean coughtOpCode;
    private short nextShort;
    private boolean messageIsDone;
    ClientMessage response;

    public MessageEncoderDecoderImpl(){
        bytes = new byte[1 << 10]; //start with 1k
        len = 0;
        zeroCounter=0;
        byteCounter=0;
        shortCounter=0;
        opCodeBytes =new byte[2];
        shortCodeBytes =new byte[2];
        coughtOpCode=false;
        msgOpCode=0;
        messageIsDone = false;
        response = null;
    }


    @Override
    public ClientMessage decodeNextByte(byte nextByte) {
        if (messageIsDone){
            reset();
            return response;
        }
        if(byteCounter<2&!coughtOpCode) {
            pushByte(nextByte);
        }
        if(byteCounter==2&!coughtOpCode){
            msgOpCode = bytesToShort(opCodeBytes);
            coughtOpCode=msgOpCode!=0;
        }
        else if(coughtOpCode) {
            switch (msgOpCode) {
                case 1:
                    if(nextByte == ';'){
                        response = new ClientMessage(msgOpCode,popString());
                        messageIsDone = true;
                        break;
                    }
                    else {
                        if(nextByte == '\0'){
                            if(zeroCounter==2){
                                break;
                            }
                            else if (zeroCounter<2) {
                                zeroCounter++;
                                pushByte(",".getBytes()[0]);
                                break;
                            }
                        }
                        else {
                            pushByte(nextByte);
                            break;
                        }
                    }

                case 2:
                    if(nextByte == ';'){
                        response = new ClientMessage(msgOpCode,popString());
                        response.getShorts().add(nextShort);
                        messageIsDone = true;
                        break;
                    }
                    else {
                        if (zeroCounter == 2){
                            shortCodeBytes[shortCounter] = nextByte;
                            shortCounter++;
                            if (shortCounter == 2){
                                nextShort = bytesToShort(shortCodeBytes);
                            }
                            break;
                        }
                        else if(nextByte == '\0'){
                            zeroCounter++;
                            pushByte(",".getBytes()[0]);
                            break;
                        }
                        else {
                            pushByte(nextByte);
                            break;
                        }
                    }
                case 3:
                    if(nextByte == ';'){
                        response = new ClientMessage(msgOpCode,"");
                        messageIsDone = true;
                        break;
                    }
                    else {
                        break;
                    }
                case 4:
                    if(nextByte == ';'){
                        response = new ClientMessage(msgOpCode,popString());
                        response.getShorts().add(nextShort);
                        messageIsDone = true;
                        break;
                    }
                    else {
                        if (shortCounter < 2){
                            shortCodeBytes[shortCounter] = nextByte;
                            shortCounter++;
                            if(shortCounter == 2){
                                nextShort = bytesToShort(shortCodeBytes);
                            }
                            break;
                        }
                        else{
                            pushByte(nextByte);
                            break;
                        }


                    }
                case 8:
                case 5:
                    if(nextByte == ';'){
                        response = new ClientMessage(msgOpCode,popString());
                        messageIsDone = true;
                        break;
                    }
                    else {
                        if (nextByte == '\0'){
                            break;
                        }
                        else {
                            pushByte(nextByte);
                            break;
                        }
                    }
                case 6:
                    if(nextByte == ';'){
                        response = new ClientMessage(msgOpCode,popString());
                        messageIsDone = true;
                        break;
                    }
                    else {
                        if(nextByte == '\0'){
                            if(zeroCounter==2){
                                break;
                            }
                            else if (zeroCounter<2) {
                                zeroCounter++;
                                pushByte(",".getBytes()[0]);
                                break;
                            }
                        }
                        else {
                            pushByte(nextByte);
                            break;
                        }
                    }
                case 7:
                    if(nextByte == ';'){
                        response = new ClientMessage(msgOpCode,popString());
                        messageIsDone = true;
                        break;
                    }
                    else {
                        break;
                    }

                case 12:
                    if(nextByte == ';'){
                        response = new ClientMessage(msgOpCode,popString());
                        messageIsDone = true;
                        break;
                    }
                    else {
                        if (nextByte == '\0'){
                            break;
                        }
                        else {
                            pushByte(nextByte);
                            break;
                        }
                    }

            }

        }
        return null; //not a line yet
    }


    @Override
    public byte[] encode(ClientMessage message) {
        short opCode = message.getOpCode();
        byte[] Op;
        Op = shortToBytes(message.getOpCode());
        byte[] zero = new byte[1];
        zero[0] = '\0';
        byte[] ender = new byte[2];
        ender[0] = ';';

        switch (opCode){

            case 9:
                String[] parts = message.getData().split(";");
                byte[] notificationType = new byte[1];
                if (message.getShorts().get(0) == 0){
                    notificationType[0] = '\0';
                }
                else {
                    notificationType[0] = '\1';
                }
                byte[] postingUser = parts[0].getBytes(StandardCharsets.UTF_8);

                byte[] content = parts[1].getBytes(StandardCharsets.UTF_8);

                ByteArrayOutputStream notificationOutput = new ByteArrayOutputStream();
                notificationOutput.write(Op,0,2);
                notificationOutput.write(notificationType,0,1);
                notificationOutput.write(postingUser,0,postingUser.length);
                notificationOutput.write(zero,0,1);
                notificationOutput.write(content,0,content.length);
                notificationOutput.write(zero,0,1);
                notificationOutput.write(ender,0,2);

                return notificationOutput.toByteArray();

            case 10:
                byte[] msgOp = new byte[2];
                msgOp = shortToBytes(message.getShorts().get(0));
                ByteArrayOutputStream ackOutput = new ByteArrayOutputStream();
                ackOutput.write(Op,0,2);
                ackOutput.write(msgOp,0,2);
                if (message.getShorts().get(0) == 8 | message.getShorts().get(0) ==7){
                    byte[] age;
                    age = shortToBytes(message.getShorts().get(1));
                    byte[] posts;
                    posts = shortToBytes(message.getShorts().get(2));
                    byte[] followers;
                    followers = shortToBytes(message.getShorts().get(3));
                    byte[] following;
                    following = shortToBytes(message.getShorts().get(4));
                    ackOutput.write(age,0,2);
                    ackOutput.write(posts,0,2);
                    ackOutput.write(followers,0,2);
                    ackOutput.write(following,0,2);
                }
                if (message.getShorts().get(0) == 4){
                    byte[] username = message.getData().getBytes(StandardCharsets.UTF_8);
                    ackOutput.write(username,0,username.length);
                    ackOutput.write(zero,0,1);
                }
                ackOutput.write(ender,0,2);
                return ackOutput.toByteArray();

            case 11:
                byte[] msgOp2 = new byte[2];
                msgOp2 = shortToBytes(message.getShorts().get(0));
                ByteArrayOutputStream ErrorOutput = new ByteArrayOutputStream();
                ErrorOutput.write(Op,0,2);
                ErrorOutput.write(zero,0,1);
                ErrorOutput.write(msgOp2,0,2);
                ErrorOutput.write(ender,0,2);

                return ErrorOutput.toByteArray();
        }

        return (message.getData() +'\0').getBytes();
    }

    private void pushByte(byte nextByte) {
        if(byteCounter<2){
            opCodeBytes[byteCounter]=nextByte;
        }

        else {
            if (len >= bytes.length) {
                bytes = Arrays.copyOf(bytes, len * 2);
            }
            bytes[len++] = nextByte;
        }

        byteCounter++;
    }

    private String popString() {
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        return result;
    }

    private short popShort(){
        short result= ByteBuffer.wrap(opCodeBytes).getShort();
        return result;
    }
    private void reset(){
        len = 0;
        zeroCounter=0;
        byteCounter=0;
        coughtOpCode=false;
        messageIsDone = false;
        nextShort = 0;
        shortCounter = 0;
        bytes = new byte[1 << 10];
    }

    public short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    public static byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }
}