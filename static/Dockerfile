FROM store/oracle/serverjre:8
WORKDIR /srv/app

COPY build/libs/killerdroid-static-3.0.jar /srv/app
COPY ssh ssh
COPY required_files required_files
COPY launcher.sh launcher.sh

ENV MAIN_PATH=/srv
ENV ANDROID_PLATFORMS=${MAIN_PATH}/android-platforms
ENV SDK=${MAIN_PATH}/sdk

#CMD java -jar killerdroid-static-3.0.jar -c config.json -s $sha
CMD ./launcher.sh

