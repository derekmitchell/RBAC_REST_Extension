# RBAC_REST_Extension
This extension to the REST API provides following functionality:  
  
•	Create New User (POST)
•	Create New Group (POST)
•	Create New Account Role (POST)
•	Retrieve User (GET) 
•	Retrieve All Users (GET)
•	Retrieve Group (GET)
•	Retrieve All Groups (GET)
•	Retrieve Account Role (GET)
•	Retrieve All Account Roles (GET)
•	Retrieve Groups for User (GET) 
•	Update User (PUT)
•	Update Group (PUT)
•	Update Account Role (PUT)
•	Add User to Group (PUT)
•	Add Account Role to User (PUT)
•	Add Account Role to Group (PUT)
•	Remove User from Group (DELETE)
•	Remove User from All Groups (DELETE)
•	Remove Account Role from Group (DELETE)
•	Remove Account Role from User (DELETE)
•	Delete User (DELETE)
•	Delete Group (DELETE)
•	Delete Account Role (DELETE) 

The REST API will use JSON for both input and output.   Authentication with the account name, user name and password will be required to access any of the above functionality.    

The REST extension consists of several JAR files, and will be deployed onto the controller in the following directories: 
ace_Rest_server-1.3.jar

This file should be copied in the following directory on the controller: 

<Controller>/custom/restExtensions 

Any older versions should be deleted.   This JAR file should be marked as executable for the user the controller runs as. 

ACE_RestServer-1.3.zip

This file contains the following two JAR files: 

•	ace_Rest_api-1.3.jar
•	ace_Rest_util-1.3.jar

It should be unzipped into the following directory on the controller: 

<Controller>/appserver/glassfish/domains/domain1/applications/controller/controller-web_war/WEB-INF/lib

Any older versions of these JAR files should be deleted.   These JAR files should be marked as executable for the user the controller runs as. 
