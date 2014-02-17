#!/usr/bin/env bash
java -Xmx1024M -Xss2M -XX:MaxPermSize=512m -XX:+CMSClassUnloadingEnabled -jar `dirname $0`/lib/sbt-launch-0.12.1.jar "$@"
