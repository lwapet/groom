#!/usr/bin/env bash
rm -rf /Users/lgitzing/Development/work/groom/injected_code/build/
gradle build
cd /Users/lgitzing/Development/work/groom/injected_code/build/classes/java/main
jar cvf Encryptor.jar a/a/Encryptor.class
dx --dex --output=Encryptor.dex Encryptor.jar
mv Encryptor.dex /Users/lgitzing/Development/work/groom/obfuscator/


