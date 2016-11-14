scp ./rest_server/build/libs/ace_Rest_server-1.3.jar derek@192.168.56.20:/home/derek/AppDynamics/Controller4.1/custom/restExtensions

scp ./build/zips/ACE_RestServer-1.3.zip derek@192.168.56.20:/tmp

ssh derek@192.168.56.20 unzip /tmp/ACE_RestServer-1.3.zip -d /home/derek/AppDynamics/Controller4.1/appserver/glassfish/domains/domain1/applications/controller/controller-web_war/WEB-INF/lib

ssh derek@192.168.56.20 chmod +x /home/derek/AppDynamics/Controller4.1/appserver/glassfish/domains/domain1/applications/controller/controller-web_war/WEB-INF/lib/ace*

ssh derek@192.168.56.20 chmod +x /home/derek/AppDynamics/Controller4.1/custom/restExtensions/ace_Rest_server-1.3.jar

