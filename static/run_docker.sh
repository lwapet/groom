#!/usr/bin/env bash
docker run -it \
	--env sha="$1" \
	-v /Users/lgitzing/Development/work/android-platforms:/srv/android-platforms \
	-v /Users/lgitzing/Library/Android/sdk:/srv/sdk \
	-v /Users/lgitzing/Development/work/Groom/static/main_docker.json:/srv/app/config.json \
	inky0/groom:latest

