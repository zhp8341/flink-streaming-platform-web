#!/bin/sh

cd `dirname $0 `

mvn clean package

docker build -t flink-streaming-web:0.0.1 .