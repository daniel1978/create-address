#!/bin/bash

docker run -d \
  --name camunda \
  -p 8080:8080 \
  camunda/camunda-bpm-platform:latest