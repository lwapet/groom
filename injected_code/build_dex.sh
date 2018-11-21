#!/usr/bin/env bash
rm -rf /Users/lgitzing/Development/work/Groom/build/
gradle build
cd /Users/lgitzing/Development/work/Groom/build/classes/java/main/
jar cvf Groom.jar *
dx --dex --output=Groom.dex Groom.jar
mv Groom.dex /Users/lgitzing/Development/work/FridaApkInstrumenter/


