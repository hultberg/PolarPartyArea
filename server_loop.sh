#!/bin/sh
while true
do
        java -Xincgc -Xmx3G -jar craftbukkit.jar nogui
        echo "If you want to completely stop the server process now, press Ctrl+C before the time is up!"
        echo "Rebooting in:"
        for i in 10 9 8 7 6 5 4 3 2 1
        do
                echo "$i..."
                sleep 1
        done
        echo "Rebooting now!"
done