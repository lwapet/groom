#!/usr/bin/env bash

#gradle build
IGRIDA_DIRECTORY=igrida_workspace

rm -r ${IGRIDA_DIRECTORY}
mkdir ${IGRIDA_DIRECTORY}
cp build/libs/killerdroid-static-3.0.jar ${IGRIDA_DIRECTORY}
cp main_killerdroid.json ${IGRIDA_DIRECTORY}
cp -r ssh ${IGRIDA_DIRECTORY}
cp -r required_files ${IGRIDA_DIRECTORY}
zip -r groom_igrida.zip ${IGRIDA_DIRECTORY}
