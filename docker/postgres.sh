#!/bin/bash

docker run -d \
  --name create-address-postgres \
  -e POSTGRES_DB=crm \
  -e POSTGRES_USER=crm-user \
  -e POSTGRES_PASSWORD=abcd1234 \
  -p 5432:5432 \
  postgres