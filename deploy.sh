#!/usr/bin/env bash

REPOSITORY=/home/ubuntu/deploy
cd $REPOSITORY

APP_NAME=springboot-intro
JAR_NAME=$(ls $REPOSITORY/build/libs/ | grep 'SNAPSHOT.jar' | tail -n 1)
JAR_PATH=$REPOSITORY/build/libs/$JAR_NAME

CURRENT_PID=$(pgrep -f $APP_NAME)

if [ -z "$CURRENT_PID" ]; then
  echo "> 종료할 프로세스 없음."
else
  echo "> 현재 실행 중인 프로세스 종료: kill -15 $CURRENT_PID"
  kill -15 "$CURRENT_PID"
  sleep 5
fi

echo "> $JAR_PATH 배포 시작"
nohup java -jar "$JAR_PATH" > /home/ubuntu/app/deploy/app.log 2>&1 &

echo "> 배포 완료!"
exit 0  # CodeDeploy에 배포 완료를 명확히 알림
