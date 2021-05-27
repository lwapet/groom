#!/bin/bash
echo "####### compiling soot sources"
sleep 2
cd ../soot_sources
mvn clean compile assembly:single
spd-say "soot sources compiled"
echo "####### compiling intrumenter"
sleep 2
cp -v target/sootclasses-trunk-jar-with-dependencies.jar ../static/libs/soot-3.4.0.jar
cp -v target/sootclasses-trunk-jar-with-dependencies.jar ../core/libs/soot-3.4.0.jar
cp -v target/sootclasses-trunk-jar-with-dependencies.jar ../database/libs/soot-3.4.0.jar
cd ../static
gradle build
spd-say "intrumenter compiled"; echo $(date)
