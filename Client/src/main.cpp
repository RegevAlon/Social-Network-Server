#include <iostream>
#include <cstdlib>
#include "../include/readerThread.h"
#include "../include/writerThread.h"
#include <stdlib.h>
#include <mutex>
#include <thread>

using namespace std;


void decode(Reader reader){
    reader.run();
}

int main (int argc, char *argv[]) {
    if (argc < 3) {
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);
    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        return 1;
    }
    std::cout << "client is open host: " + host + " port: " + to_string(port) << std::endl;
    std::mutex mutex;
    Writer writer(&connectionHandler, mutex);
    Reader reader(&connectionHandler, mutex);
    thread writerThread(&Writer::run, &writer);
    decode(reader);
    writerThread.join();
    std::cout << "client is closed on host: " + host + " port: " + to_string(port) << std::endl;

    return 0;
}