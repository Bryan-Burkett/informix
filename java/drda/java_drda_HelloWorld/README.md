#Java DRDA Hello World Sample Application

##Structure

###The application contains these files:

####src/com/ibm/informix/java_drda_HelloWorld.java

This file contains sample data interacting with a database.

####src/com/ibm/informix/HelloWorldServlet.java

This file is the servlet for the application.

####WebContent/index.jsp

This file is the homepage.

####WebContent/WEB-INF/web.xml

This file contains details about deploy to the web.

####build.gradle

This file gathers dependencies and builds the .war file needed to deploy to Bluemix.

####manifest.yml

This file gives details to Bluemix about the application.

####.project

This file contains properties about the project.

####.classpath

This file contains properties about the classpath of the application.

##Deploy application to Bluemix

###Option 1: Deploying to Bluemix from a local machine

####Requirements:

Git - you use to download the application. <Link to download Git>

CloudFoundry CLI -  you use to push the application to Bluemix. <Link to download CLI>

Gradle -  you use to get dependencies and build the application. <Link to download Gradle>
	
//set environment variable?

####Procedure:

Clone repository
	
	git clone <someway to not clone all files>
	
//navigate to project

Get corresponding driver from <website to download>
	
	WebContent/WEB-INF/lib/
	 
//navigate to home directory of a project (with .gradle file)

Run gradle
	
	gradle build <AppName>
	
Connect to Bluemix
	
	cf api https://api.ng.bluemix.net
	
Log into Bluemix
	
	cf login
	
Enter user and pass
	
	jondoe@ibm.com
	password
	
Push application
	
	cf push <YourAppName>
	 
	 
// Again everything is in one repository... Harder to deploy from JazzHub

###Option 2: Deploy from JazzHub

I don't know how to do this yet, but dataworks docs says

See the JazzHub documentation for full information, but the steps are as follows:

Open this project in a new browser window.

Click EDIT CODE, and log in to JazzHub if you are not already authenticated.

Click Fork.

Provide a name for your copy of the sample and click Save.

When the project is copied into your repository, click BUILD & DEPLOY.

Click Project Settings.

Select Deploy to Bluemix, keep the other default values, and click Save.

On BUILD & DEPLOY, click ADVANCED, and click add a builder.

Keep the default values in the Add Builder page, and click SAVE.

Click REQUEST BUILD.

When the build has completed and you see a green check mark, click add a stage.

Provide a unique application name and click SAVE.

In the pipeline, drag the Build button to the deployment space to deploy the application to Bluemix. The number of times that the application has been built is displayed in the button.
