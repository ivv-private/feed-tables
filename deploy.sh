#!/bin/bash

echo shutdown server
~/programs/tomcat-feed/bin/shutdown.sh > /dev/null

while (netstat -na | grep '0.0.0.0:80\ .*LISTENING' > /dev/null)
do 
 echo waiting for shutdown
 sleep 1
done

echo cleanup
rm ~/programs/tomcat-feed/webapps/* -fr
rm ~/programs/tomcat-feed/conf/Catalina/localhost/* -f

echo deploy
cp feed-tables.war ~/programs/tomcat-feed/webapps/

echo startup server
~/programs/tomcat-feed/bin/startup.sh
