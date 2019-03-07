docker run \
	--env var="$1" \
	-v /home/lgitzing/apks/legacy:/srv/apks \
	-v /home/lgitzing/Android/sdk/platforms:/srv/android-platforms \
	-v /home/lgitzing/Android/sdk:/srv/sdk \
	-v /home/lgitzing/groom/static_docker:/home \
	inky0/groom_static_java:latest

