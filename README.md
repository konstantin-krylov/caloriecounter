[![Build Status](https://travis-ci.com/konstantin-krylov/caloriecounter.svg?branch=master)](https://travis-ci.com/konstantin-krylov/caloriecounter)
[![codecov](https://codecov.io/gh/konstantin-krylov/caloriecounter/branch/master/graph/badge.svg)](https://codecov.io/gh/konstantin-krylov/caloriecounter)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.epam%3Acaloriecounter&metric=coverage)](https://sonarcloud.io/dashboard?id=com.epam%3Acaloriecounter)
# Pet Project For the Greater Good

It is a simple project to create and keep a calorie diary.

## Startup
It is a **Spring Boot** project using *gradle*. So you may run it via cli:
    
    ./gradlew bootRun

##### Start PostgreSQL Container (downloads image if not found)
If you don't have **PostgreSQL** installed, you can run the database in a **Docker Container** via cli:
```
docker run -d -p 6604:5432 --env POSTGRES_DB=caloriecounter --name cc-postgres postgres:9.5.10 
```
## Dockerize Calorie Counter
##### Build jar
``
./gradlew bootJar
``
##### Build Docker image
``
docker build -t caloriecounter .
``
##### Run Docker container
``
docker run --name cc-app -p 3333:8080 -v ~/db/migration:/var/migration -e server=cc-postgres -e port=5432 -e dbuser=postgres -e dbpassword=postgres --link cc-postgres:postgres -d caloriecounter
``
##### enter Docker container
``
docker exec -t -i cc-app /bin/bash
``

## Why should I run pet projects?
1. It is **fun**.
1. It helps to **learn** and **practice**.
1. It does good as an addition to a **CV**.