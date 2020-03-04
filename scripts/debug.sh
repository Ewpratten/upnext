#! /bin/bash

adb kill-server
adb -d forward tcp:5601 tcp:5601