docker run \
	--env var="koler-001fe5c473b8890a3883b7f3fbc65ca7133ac8a5e9c6b3f0f79a6edd37ac9390.apk" \
	-v /Users/lgitzing/Development/work/apks/legacy:/srv/apks \
	-v /Users/lgitzing/Development/work/android-platforms:/srv/android-platforms \
	-v /Users/lgitzing/Library/Android/sdk:/srv/sdk \
	-v /Users/lgitzing/Development/work/groom/static_docker:/home \
	--name static_docker_java inky0/groom_static_java:latest

