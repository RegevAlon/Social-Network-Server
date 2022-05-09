LDFLAGS:=-lboost_system -lboost_thread -pthread

#REGISTER tal qrst 12/34/5678
all: link

link: compile
	g++ -o bin/main bin/main.o bin/readerThread.o bin/writerThread.o bin/connectionHandler.o $(LDFLAGS)

compile: src/main.cpp src/connectionHandler.cpp src/readerThread.cpp src/writerThread.cpp
	g++ -g -c -Wall -Weffc++ -std=c++11 -o bin/main.o src/main.cpp
	g++ -g -c -Wall -Weffc++ -std=c++11 -o bin/readerThread.o src/readerThread.cpp
	g++ -g -c -Wall -Weffc++ -std=c++11 -o bin/writerThread.o src/writerThread.cpp
	g++ -g -c -Wall -Weffc++ -std=c++11 -o bin/connectionHandler.o src/connectionHandler.cpp


clean:
	rm -rf bin/*
