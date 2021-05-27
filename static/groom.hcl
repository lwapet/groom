job "lgitzing_groom" {
    datacenters = ["dc1"]
    type = "batch"
    parameterized {
    	payload       = "required"
    }

    group "run1" {
        count = 1
        restart {
            attempts = 2
            interval = "30m"
            delay = "15s"
            mode = "fail"
        }
        ephemeral_disk {
            size = 300
        }
        task "run" {
            driver = "docker"
            config {
                image = "inky0/groom:v4"
		volumes = [
			"/mnt/widecore24/lgitzing/Android/sdk/platforms:/srv/android-platforms",
			"/mnt/widecore24/lgitzing/Android/sdk:/srv/sdk",
			"/mnt/widecore24/lgitzing/main_docker.json:/srv/app/config.json"
		]
            }
            resources {
                cpu = 2000
                memory = 7000
            }
 	dispatch_payload {
        	file = "sha.txt"
      	}
        }
    }
}
