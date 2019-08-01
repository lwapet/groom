#!/bin/bash
java -jar killerdroid-static-3.0.jar -c config.json -s $(cat /local/sha.txt)
