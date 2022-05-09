#include "../include/readerThread.h"

using std::string;

Reader::Reader(ConnectionHandler *connectionHandler, std::mutex& mutex):connectionHandler(connectionHandler), mutex(mutex) {}

void Reader::run() {
    while (!ReaderShouldStop){
        string answer;
        if(connectionHandler->getLine(answer)) {
            if (!answer.compare("") == 0) {
                std::cout << answer << std::endl;
            }
            if(answer.compare("ACK 3 ")==0) {
                ReaderShouldStop=true;
                WriterShouldStop=true;
                break;
            }
            else
                stopTyping=false;
        }
    }
}
