//
// Created by User on 04/01/2022.
//

#ifndef SPLTEST_READERTHREAD_H
#define SPLTEST_READERTHREAD_H

#include "../include/connectionHandler.h"
#include <mutex>


class Reader{

private:
    ConnectionHandler* connectionHandler;
    std::mutex& mutex;

public:
    Reader(ConnectionHandler* connectionHandler, std::mutex& mutex);

    void run();
};


#endif //SPLTEST_READERTHREAD_H
