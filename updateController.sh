#!/bin/sh
gradle build
echo "Require to be in ADDev Mode !!!"
echo "--"
echo "updating $CONTROLLER_DIR"
echo ""
cp rest_api/build/libs/ace_Rest_api-1.2.jar $CONTROLLER_DIR/domains/domain1/applications/controller/controller-web_war/WEB-INF/lib
cp rest_util/build/libs/ace_Rest_util-1.2.jar $CONTROLLER_DIR/domains/domain1/applications/controller/controller-web_war/WEB-INF/lib

ls -al $CONTROLLER_DIR/domains/domain1/applications/controller/controller-web_war/WEB-INF/lib/ace*.jar

