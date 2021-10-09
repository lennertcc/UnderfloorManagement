nohup sudo java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8000 -jar /home/pi/UnderfloorManagement/UnderfloorManagement-1.2-SNAPSHOT.jar &
echo $!
echo $! > save_pid.txt