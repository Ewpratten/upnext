#! /bin/bash

adb kill-server
adb -d forward tcp:5601 tcp:5601
adb forward tcp:4444 localabstract:/adb-hub
adb connect 127.0.0.1:4444