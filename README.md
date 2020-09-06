[![Build Status](https://travis-ci.com/konstantin-krylov/caloriecounter.svg?branch=master)](https://travis-ci.com/konstantin-krylov/caloriecounter)
[![codecov](https://codecov.io/gh/konstantin-krylov/caloriecounter/branch/master/graph/badge.svg)](https://codecov.io/gh/konstantin-krylov/caloriecounter)
# Pet Project For the Greater Good

It is a simple project to create and keep a calorie diary.

## Startup
It is a **Spring Boot** project using *gradle*. So you may run it via cli:
    
    ./gradlew bootRun

##### Start PostgreSQL Container (downloads image if not found)
If you don't have **PostgreSQL** installed, you can run the database in a **Docker Container** via cli:
```
docker run -p 6604:5432 --env POSTGRES_DB=caloriecounter --name cc-postgres postgres:9.5.10 
```

## Why should I run pet projects?
1. It is **fun**.
1. It helps to **learn** and **practice**.
1. It does good as an addition to a **CV**.