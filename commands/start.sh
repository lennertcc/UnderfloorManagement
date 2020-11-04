nohup sudo java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8000 -jar /home/pi/UnderFloorManagement/UnderfloorManagement-1.1-SNAPSHOT.jar
echo $!
echo $! > save_pid.txt
