#!/usr/bin/env bash

REPOSITORY=/opt/codedeploy-agent/deployment-root/360ceed4-d76d-48aa-a43b-e2258c185bf0/d-3KR2T23IA/deployment-archive
cd $REPOSITORY

APP_NAME=springboot-intro
JAR_NAME=$(ls $REPOSITORY/build/libs/ | grep 'HairPower_BE-0.0.1-SNAPSHOT.jar' | tail -n 1)
JAR_PATH=$REPOSITORY/build/libs/$JAR_NAME

CURRENT_PID=$(pgrep -f $APP_NAME)

if [ -z $CURRENT_PID ]
then
  echo "> 종료할것 없음."
else
  echo "> kill -9 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

echo "> $JAR_PATH 배포"
nohup java -jar $JAR_PATH > /dev/null 2> /dev/null < /dev/null &
