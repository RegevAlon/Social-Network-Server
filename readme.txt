Server:
1) mvn clean
2) mvn compile

To run TPC:
mvn exec:java -Dexec.mainClass="bgu.spl.net.srv.TPCmain" -Dexec.args="7777"

To run Reactor:
mvn exec:java -Dexec.mainClass="bgu.spl.net.srv.reactorMain" -Dexec.args="7777"

Client:
1) make clean
2) make
3) cd bin
4) ./main 127.0.0.1 7777

Examples:
register:
register ben 123 07/07/1995

Login:
login ben 123

Logout:
logout

Follow/Unfollow:
follow 0 name - follow name
follow 1 name - unfollow name

Post:
post content

PM:
pm name content

LOGSTAT:
logstat

Stats:
stat name1|name2|name3

Block:
block name
----------------------------------
filtered msg is in Database
Insert filters with small letters
----------------------------------
