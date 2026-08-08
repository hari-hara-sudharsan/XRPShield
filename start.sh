#!/bin/bash
cd backend
mvn clean package -DskipTests
java -jar target/xrpshield-backend-1.0.0-SNAPSHOT.jar
