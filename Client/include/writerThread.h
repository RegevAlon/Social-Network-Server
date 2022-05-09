//
// Created by User on 04/01/2022.
//

#ifndef SPLTEST_WRITERTHREAD_H
#define SPLTEST_WRITERTHREAD_H

#include <mutex>
#include "connectionHandler.h"


class Writer {

private:
    ConnectionHandler* connectionHandler;
    std::mutex& mutex;

public:
    Writer(ConnectionHandler* connectionHandler, std::mutex& mutex);
    void run();
    void shortToBytes(short num, char* bytesArr);
};


#endif //SPLTEST_WRITERTHREAD_H
