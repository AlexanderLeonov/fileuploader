# Installation

## Prerequisites:

Following needs to be installed in order to run the project:
- [Java 8 SDK](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Maven >= 3.6](https://maven.apache.org/download.cgi)
- [Chrome](https://www.google.com/chrome/) browser

Please follow installation instructions for the software.

# Working with the project

## Open in IntelliJ IDEA

- open root folder of the project in IntelliJ IDEA
- import pom.xml as maven project
- open project properties
- select SDK for project
- close project properties
- after indexing project files IntelliJ IDEA will detect angular framework and will suggest to import its settings.
Import it if you need to work with UI separately.

## Configuring project properties

Project properties can be found in the following file:

- `/src/main/resources/application.properties`
  * set `storage.type` parameter to the type of storage service:
    * `file-storage` will store files in the folder specified by `storage.location` parameter;
    * `memory-storage` will use in-memory storage service;
      * this type of storage is volatile! All stored files will be lost upon stopping of the application.
      It is implemented just for illustration purposes.
  * set `server.port` to any free port on which web server will be listening.
    * if you have changed the port and need to run UI in the debug mode separately from the API Spring Boot application
    then you'll need to update port number in the `src/ui/proxy.conf.json`; in this case dev server will proxy
    UI's requests to the API without requiring API to have proper CORS configuration.

# Building and running the project

## Running project in IntelliJ IDEA

Create new configuration:
- click `Add configuration` on top toolbar
- type some meaningful configuration name like 'Run app' in the `Name` field
- add new `Maven` configuration in the dialog
- add command line:
  - `clean spring-boot:run`
- add `Build` step to `Before launch` list
- close the dialog
- run the configuration

## Running project with maven from command line

- go to the root folder of the project
- run the following command:
  - `mvn clean spring-boot:run`

## Working with application

After application starts, open any browser with the following url:
  - `http://localhost:8900`
  - port in the url should be what is specified in the `application.properties` file in `server.port` parameter.

## Running unit tests on the project

- go to the root folder of the project
- run the following command:
  - `mvn test`

## Building release jar

- go to the root folder of the project
- run the following command:
  - `mvn package`
  
## Running release jar

- build release jar;
- go to the `/target` folder that was created after build;
- run the following command:
  - `java -jar file-uploader-service-0.1.0.jar`

## Other notes

* All unit testing is performed for demo purposes only and does not fully cover all execution branches and/or components
of the application.
* Unit tests in UI project are only written for `AppComponent` just to provide some example of how it is supposed to be done.
Unit tests for other components are fixed in such a way so that they would not fail project build, but no real testing
is performed in any of them.

# Documentation

## Swagger documentation

Apart from this README.md there is a Swagger documentation for the file uploader API available
while running project at [http://localhost:<server.port>/swagger-ui.html]()

However, it looks like Swagger UI does not support uploading array of files as of yet:
[see here](https://github.com/OAI/OpenAPI-Specification/issues/254). 
Due to this it cannot be used to check file uploading POST endpoint. The rest of operations work fine though.