#Description
TedTalk Rest API demo.

We have an insatiable curiosity and what better way than to build a knowledge sharing platform to help
us on our way. Our mission is to build a new knowledge sharing platform where our colleagues can keep 
their curiosity going. To start off, we would like to have a list of TedTalks for
our pilot system. 

#Functional requirements
##Backend
REST api should provide CRUD (Create/Read/Update/Delete) functionality.

- {C} - Be able to add a new TedTalk
- {R} - Be able to search for a TedTalk based on
  * Author
  * Title
  * Views
  * Likes
- {U} - Be able to update a TedTalk based on the id
- {D} - Be able to delete a TedTalk based on the id
##Front-end
We’re not looking for any front-end implementation.

#Prerequisites
* MySql 5.7.31 or later
* Java 11
* Docker (optional)

#Quick Start (without using docker)

<code>git clone https://github.com/dmitri-sirobokov/ted.git</code>

<code>mvn clean install</code>

Using MySql Management console create new database, `ted-talk`

Copy src/test/resources/data.csv file to a temporary location, e.g c:\data\ted\
The file contains initial records to populate the database. It will be read once during application 
startup and then deleted. To specify other location of the initial data, you can modify 
`ted.initial-db-file-csv` property in configuration file /src/main/resources/application.yaml

Run application using your favorite development tool, e.g. IntelliJ or Eclipse, 
or run compiled jar from terminal with `java -jar` command.

#Swagger
Navigate in browser to http://localhost:8080/swagger-ui.html.

You should see Swagger documentation.

#Tests
Project includes the following tests:
* TedControllerE2E: End-to-end test of the complete stack, using a number of api calls to verify a simple user CRUD-flow.
* TedControllerRestIT: Integration test of the REST api, using TestRestTemplate, with mocked repository
* TedControllerMvcIT: Integration test of the Mvc controller using MockMvc, with mocked service class.
* TedRepositoryTests: Integration test of the repository using DataJpaTest, using in-memory database.
* TedServiceTests: Unit test of the TedService class.

Manual e2e tests of the api can be performed from Swagger interface.
