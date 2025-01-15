job "picit-api" {
  datacenters = ["iutlens"]

  type = "service"

  group "api" {
    count = 1

    # The "network" block specifies the network configuration for the allocation
    # including requesting port bindings.
    #
    # For more information and examples on the "network" block, please see
    # the online documentation at:
    #
    #     https://developer.hashicorp.com/nomad/docs/job-specification/network
    #
    network {
      mode = "bridge"
      port "api" {
        to = 8081
      }
      port "mongo" {
        to = 27017
      }
    }

    service {
      name = "picit-api"
      port = "api"

      check {
        type     = "http"
        path     = "/actuator/health"
        interval = "10s"
        timeout  = "2s"
      }
    }

    task "api" {
      driver = "docker"

      config {
        image = "dh-iutl.univ-artois.fr/picit/picit-api:latest"
        ports = ["api"]
      }

      env {
        SPRING_DATA_MONGODB_URI = "mongodb://127.0.0.1:${NOMAD_PORT_mongo}"
      }

      resources {
        cpu    = 500
        memory = 256
      }

      service {
        name = "picit-api"
        port = "api"

        tags = [
          "traefik.enable=true",
          "traefik.http.routers.picit-api-nossl.entrypoints=web",
          "traefik.http.routers.picit-api-nossl.rule=Host(`picit-api.dev-iutl.univ-artois.fr`)",
          "traefik.http.services.picit-api.loadbalancer.sticky",
        ]
      }
    }

    task "mongodb" {
      driver = "docker"

      config {
        image = "mongo:latest"
        ports = ["mongo"]
      }

      resources {
        cpu    = 500
        memory = 512
      }
    }
  }
}
