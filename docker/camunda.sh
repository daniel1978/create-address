#!/bin/bash

docker rm -f camunda

docker run -d \
  --name camunda \
  -p 8080:8080 \
  mac-camunda-bpm-platform

# camunda/camunda-bpm-platform:latest