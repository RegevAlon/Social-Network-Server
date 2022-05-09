//
// Created by User on 04/01/2022.
//
#include <stdlib.h>
#include <mutex>
#include <iomanip>
#include "boost/lexical_cast.hpp"
#include <boost/algorithm/string.hpp>
#include "../include/writerThread.h"

using boost::asio::ip::tcp;
using std::string;
using std::mutex;
using std::cin;
using std::cout;
using std::endl;

Writer::Writer(ConnectionHandler *connectionHandler, std::mutex& mutex): connectionHandler(connectionHandler), mutex(mutex){}

void Writer::run() {
//    while (connectionHandler->isConnected() && !shouldStop) {
    while (!WriterShouldStop) {
        if(!stopTyping) {
            const short bufsize = 1024;
            char buf[bufsize];
            std::cin.getline(buf, bufsize);
            string line(buf);
            string opCode = line.substr(0, line.find(' '));
            boost::algorithm::to_lower(opCode);
            string restLine = line.substr(line.find_first_of(' ') + 1);
            string toSend = "";
            if (opCode.compare("register") == 0) {
                char bytes[2];
                short opcode = 1;
                shortToBytes(opcode, bytes);
                toSend.push_back(bytes[0]);
                toSend.push_back(bytes[1]);
                string userName = restLine.substr(0, restLine.find_first_of(' '));
                restLine = restLine.substr(restLine.find_first_of(' ')+1);
                string password = restLine.substr(0,restLine.find_first_of(' '));
                string birthday = restLine.substr(restLine.find_first_of(' ')+1);
                toSend += userName + '\0' + password + '\0' + birthday + '\0' + ';';
                connectionHandler->sendLine(toSend);

            } else if (opCode.compare("login") == 0) {
                char bytes[2];
                short opcode = 2;
                shortToBytes(opcode, bytes);
                toSend.push_back(bytes[0]);
                toSend.push_back(bytes[1]);
                string userName = restLine.substr(0, restLine.find_first_of(' '));
                restLine = restLine.substr(restLine.find_first_of(' ')+1);
                string password = restLine.substr(0,restLine.find_first_of(' '));
                string captcha = restLine.substr(restLine.find_first_of(' ')+1);
                toSend += userName + '\0' + password + '\0';

                if (captcha == "0"){
                    shortToBytes(0, bytes);
                    toSend.push_back(bytes[0]);
                    toSend.push_back(bytes[1]);
                } else{
                    shortToBytes(1, bytes);
                    toSend.push_back(bytes[0]);
                    toSend.push_back(bytes[1]);
                }
                toSend += ';';
                connectionHandler->sendLine(toSend);

            } else if (opCode.compare("logout") == 0) {
                char bytes[2];
                short opcode = 3;
                shortToBytes(opcode, bytes);
                toSend.push_back(bytes[0]);
                toSend.push_back(bytes[1]);
                toSend += ';';
                stopTyping=true;
                connectionHandler->sendLine(toSend);

            } else if (opCode.compare("follow") == 0) {
                char bytes[2];
                short opcode = 4;
                shortToBytes(opcode, bytes);
                toSend.push_back(bytes[0]);
                toSend.push_back(bytes[1]);

                string followOp = restLine.substr(0, restLine.find_first_of(' '));
                string username = restLine.substr(restLine.find_first_of(' ') + 1);

                if (followOp == "0"){
                    shortToBytes(0, bytes);
                    toSend.push_back(bytes[0]);
                    toSend.push_back(bytes[1]);
                } else{
                    shortToBytes(1, bytes);
                    toSend.push_back(bytes[0]);
                    toSend.push_back(bytes[1]);
                }
                toSend += username + ';';

                connectionHandler->sendLine(toSend);

            } else if (opCode.compare("post") == 0) {
                char bytes[2];
                short opcode = 5;
                shortToBytes(opcode, bytes);
                toSend.push_back(bytes[0]);
                toSend.push_back(bytes[1]);

                string content = restLine;

                toSend += content + '\0' + ';';
                connectionHandler->sendLine(toSend);

            } else if (opCode.compare("pm") == 0) {
                char bytes[2];
                short opcode = 6;
                shortToBytes(opcode, bytes);
                toSend.push_back(bytes[0]);
                toSend.push_back(bytes[1]);

                string username = restLine.substr(0, restLine.find_first_of(' '));
                string content = restLine.substr(restLine.find_first_of(' ') + 1);

                auto t = std::time(nullptr);
                auto tm = *std::localtime(&t);

                std::ostringstream oss;
                oss << std::put_time(&tm, "%d-%m-%Y %H:%M");
                auto date = oss.str();

                toSend += username + '\0' + content + '\0' + date + '\0' + ';';
                connectionHandler->sendLine(toSend);

            } else if (opCode.compare("logstat") == 0) {
                char bytes[2];
                short opcode = 7;
                shortToBytes(opcode, bytes);
                toSend.push_back(bytes[0]);
                toSend.push_back(bytes[1]);
                toSend += ';';
                connectionHandler->sendLine(toSend);

            } else if (opCode.compare("stat") == 0) {
                char bytes[2];
                short opcode = 8;
                shortToBytes(opcode, bytes);
                toSend.push_back(bytes[0]);
                toSend.push_back(bytes[1]);

                string userlist = restLine;

                toSend += userlist + '\0' + ';';
                connectionHandler->sendLine(toSend);

            } else if (opCode.compare("block") == 0) {
                char bytes[2];
                short opcode = 12;
                shortToBytes(opcode, bytes);
                toSend.push_back(bytes[0]);
                toSend.push_back(bytes[1]);

                string username = restLine;

                toSend += username + '\0' + ';';
                connectionHandler->sendLine(toSend);

            }
        }
    }
}

void Writer::shortToBytes(short num, char* bytesArr){
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}
