//
// Created by User on 04/01/2022.
//

#include "../include/connectionHandler.h"
#include <boost/asio/ip/tcp.hpp>


using boost::asio::ip::tcp;

using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;

bool WriterShouldStop=false;
bool ReaderShouldStop=false;
bool stopTyping=false;
int byteCounter = 0;
char opCodeBytes[2];
int shortCounter = 0;
char shortBytes[2];
short opCode = 0;
short shortCode = 0;
bool coughtOpCode = false;
bool coughtShortCode = false;
bool messageIsDone = false;
string nextMessage;
ConnectionHandler::ConnectionHandler(string host, short port): host_(host), port_(port), io_service_(), socket_(io_service_), connected(
        false){}

ConnectionHandler::~ConnectionHandler() {
    close();
}

bool ConnectionHandler::connect() {
    try {
        tcp::endpoint endpoint(boost::asio::ip::address::from_string(host_), port_); // the server endpoint
        boost::system::error_code error;
        socket_.connect(endpoint, error);
        if (error)
            throw boost::system::system_error(error);
    }
    catch (std::exception& e) {
        return false;
    }
    return true;
}

bool ConnectionHandler::getBytes(char bytes[], unsigned int bytesToRead) {
    size_t tmp = 0;
    boost::system::error_code error;
    try {
        while (!error && bytesToRead > tmp ) {
            tmp += socket_.read_some(boost::asio::buffer(bytes+tmp, bytesToRead-tmp), error);
        }
        if(error)
            throw boost::system::system_error(error);
    } catch (std::exception& e) {
        return false;
    }
    return true;
}

bool ConnectionHandler::sendBytes(const char bytes[], int bytesToWrite) {
    int tmp = 0;
    boost::system::error_code error;
    try {
        while (!error && bytesToWrite > tmp ) {
            tmp += socket_.write_some(boost::asio::buffer(bytes + tmp, bytesToWrite - tmp), error);
        }
        if(error)
            throw boost::system::system_error(error);
    } catch (std::exception& e) {
        return false;
    }
    return true;
}

bool ConnectionHandler::getLine(std::string& line) {
    return getFrameAscii(line, '\0');
}

bool ConnectionHandler::sendLine(std::string& line) {
    return sendFrameAscii(line, '\0');
}


bool ConnectionHandler::getFrameAscii(std::string& frame, char delimiter) {
    char ch;

    // Stop when we encounter the null character.
    // Notice that the null character is not appended to the frame string.
    try {

            while (getBytes(&ch, 1))
            {

                if (messageIsDone){
                    frame.append(nextMessage);
                    reset();
                    return true;
                }
                if (byteCounter < 2){
                    opCodeBytes[byteCounter] = ch;
                    byteCounter++;
                    if (byteCounter == 2){
                        opCode = bytesToShort(opCodeBytes);
                        coughtOpCode = true;
                    }
                }

                else if(coughtOpCode){
                    switch (opCode) {
                        case 9:
                            if (ch == ';'){
                                messageIsDone = true;
                                break;
                            }
                            else if (byteCounter == 2){
                                nextMessage.append("NOTIFICATION ");
                                if (ch == '\0'){
                                    nextMessage.append("PM ");
                                    byteCounter++;
                                    break;
                                }
                                else{
                                    nextMessage.append("Public ");
                                    byteCounter++;
                                    break;
                                }
                            }
                            else{
                                if (ch == '\0'){
                                    nextMessage.append(" ");
                                    byteCounter++;
                                    break;
                                }
                                else{
                                    nextMessage.append(1,ch);
                                    break;
                                }

                            }

                        case 10:
                            if (byteCounter == 2){
                                nextMessage.append("ACK");
                                nextMessage.append(" ");
                            }
                            if (ch == ';'){
                                messageIsDone = true;
                                break;
                            }
                            if (!coughtShortCode){
                                shortBytes[shortCounter] = ch;
                                shortCounter++;
                                if (shortCounter == 2){
                                    shortCode = bytesToShort(shortBytes);
                                    nextMessage += std::to_string((shortCode));
                                    nextMessage.append(" ");
                                    coughtShortCode = true;
                                    shortCounter = 0;
                                }
                                byteCounter++;
                                break;
                            }
                            if (coughtShortCode){
                                switch (shortCode) {
                                    case 4:
                                        if (ch == '\0'){
                                            break;
                                        }
                                        else{
                                            nextMessage.append(1,ch);
                                            break;
                                        }
                                    case 7:
                                    case 8:
                                        shortBytes[shortCounter] = ch;
                                        shortCounter++;
                                        if (shortCounter == 2){
                                            nextMessage += std::to_string((bytesToShort(shortBytes)));
                                            nextMessage.append(" ");
                                            shortCounter = 0;
                                        }
                                        break;
                                }
                                break;
                            }
                        case 11:
                            if (ch == '\0' && byteCounter == 2){
                                nextMessage.append("ERROR");
                                nextMessage.append(" ");
                                byteCounter++;
                                break;
                            }
                            if (ch == ';'){
                                messageIsDone = true;
                                break;
                            }
                            else{
                                shortBytes[shortCounter] = ch;
                                shortCounter++;
                                if (shortCounter == 2){
                                    nextMessage += std::to_string((bytesToShort(shortBytes)));
                                }
                                byteCounter++;
                                break;
                            }

                    }
                }



                return false;
            }
        return false;
        }
    catch (std::exception& e) {
        return false;
    }
    return true;
}


bool ConnectionHandler::sendFrameAscii(const std::string& frame, char delimiter) {
    bool result=sendBytes(frame.c_str(),frame.length());
    if(!result) return false;
    return sendBytes(&delimiter,1);
}

// Close down the connection properly.
void ConnectionHandler::close() {
    try{
        socket_.close();
    } catch (...) {}
}

bool ConnectionHandler::isConnected() {
    return connect();
}

short ConnectionHandler::bytesToShort(char *bytesArr) {
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}

void ConnectionHandler::reset() {
    byteCounter = 0;
    shortCounter = 0;
    opCode = 0;
    coughtOpCode = false;
    messageIsDone = false;
    coughtShortCode = false;
    shortCode = 0;
    nextMessage = "";

}
