A template that can be used to build a back end server side java application.

This simple one model (Customer) application packaged as a WAR has the following technologies implemented:

--- REST (Resource) Layer ---
1. REST layer that supports XML (JaxB) and JSON (Jackson) request and responses. Jersey JAX-RS implementation.
2. Basic Authorization using Spring security pointcuts. (Need HTTPS to complete the securing of the API)
3. Profiling HTTP requests with Around aspects (AspectJ).
4. JMS (Artemis) to asynchronously handle the results of the profiling of requests. 

--- Service Layer ---
1. Validation using both annotation (Hibernate's JSR 380 Bean Validation API implementation) and Spring's Validator framework.

--- Data Access Layer ---
There are 4 implementations of the DAO layer:
1. JDBC
2. MyBatis
3. Hibernate
4. JPA (Hibernate's implementation)

Each implementation is configured with caching (EhCache).


--- Documentation ---
Enunciate is used to create HTML documentation of your services, scraped from your JavaDocs. It builds
client-side libraries (Java, .NET, iOS, Ruby, JavaScript, GWT) for developers who want to use the API.
Creates the Interface Definition Documents (e.g. WSDL, WADL, XML-Schema).

The output of enunciate after the build will be in the project base directory under "api-docs". To open the
generated documentation in a web browser click on the index.html file.


--- Unit Testing ---
There are unit tests (JUnit and Mockito) for the REST (Resource) and Service layers.

Unit testing uses an embedded H2 database along with an embedded Artemis messaging service.

DBUnit is used to provide the embedded H2 database in the same state between test methods.

Liquibase is used to provided database source control. It creates the tables in the embedded H2 database. 

Unit testing doesn't require any standalone services like a database or a messaging server since they are both embedded.

To build and run the unit tests: mvn test


--- Integration Testing ---
Integration testing uses a Jetty HTTP Server, a standalone MySql database along with a standalone Artemis messaging service.

HttpClient and Jersey's JAX-RS HTTP client are used to test the REST API all the way through to the database.

DBUnit is used to provide the standalone MySql database in the same state between test methods. It is also used
to backup the database, configure the test data and after the tests are run set the database back to its pre-test state.

Liquibase is used to provided database source control. It creates the tables in the standalone MySql database if they do not exist.


--- Performance Testing ---
Performance testing, like Integration Testing, uses a Jetty HTTP Server, a standalone MySql database along with a 
standalone Artemis messaging service. Performance testing is run the integration testing phase after the integration tests.

Performance testing is done using JMeter. JMeter creates many threads simulating user requests. 

Each thread will make the following calls:
1. GET all customers.
2. POST new customer.
3. GET verify customer created.
4. PUT newly created customer.
5. GET verify customer updated.
6. DELETE newly created customer.
7. GET verify customer has been deleted.


--- Integration and Performance Testing Resources ---
1. The Jetty HTTP Server is already part of the POM file as a plugin so there is nothing to do there. 
2. Integration testing was done using MySql 8.0.15. Download and install it then create a schema called TestDB.
   Running the integration tests (Liquibase) will automatically create the necessary tables for you. Installing the
   database will have caused you to create a user with all privileges. Update the application.properties file 
   in the project with your database username and password.
3. Integration testing was done using Artemis 2.6.4. Download and install it. Edit the <your broker>/etc/broker.xml
   file add the following to the <addresses> section of the file:
   <address name="events">
      <anycast>
         <queue name="events"/>
      </anycast>
   </address>
4. Start up the database and the messaging broker.

--- Building and Running Tests ---
5. To run just the unit tests: mvn test
6. To run just the integration and performance tests: mvn verify -Pskip.unit.tests
7. To run everything: mvn verify

There are 4 implementations of the DAO layer. The default is set to JDBC. In order to change which 
DAO implementation is run you need to update 2 places: 

pom.xml
<plugin>
   <artifactId>maven-surefire-plugin</artifactId>
   <version>2.22.1</version>
   <configuration>
      <excludes>
         <!-- Comment out the <exclude> of the version you want to run. --> 
         <exclude>com.rest.dao.impl.jpa.*Test.java</exclude> <!-- JPA -->
         <!-- <exclude>com.rest.dao.impl.jdbc.*Test.java</exclude> --> <!-- JDBC -->
         <exclude>com.rest.dao.impl.mybatis.*Test.java</exclude> <!-- MyBatis -->
         <exclude>com.rest.dao.impl.hibernate.*Test.java</exclude> <!-- Hibernate -->
      </excludes>
      ...
   </configuration>
</plugin>

applicationContext.xml
<!-- Comment in the version you want to run. --> 
<!-- <bean id="customerDAO" class="com.rest.dao.impl.jpa.CustomerDAOImpl"/> --> <!-- JPA -->
<bean id="customerDAO" class="com.rest.dao.impl.jdbc.CustomerDAOImpl"/> <!-- JDBC -->
<!-- <bean id="customerDAO" class="com.rest.dao.impl.mybatis.CustomerDAOImpl"/> --> <!-- MyBatis -->
<!-- <bean id="customerDAO" class="com.rest.dao.impl.hibernate.CustomerDAOImpl"/> --> <!-- Hibernate -->


--- Calling the API ---
Integration testing covers calling all scenarios of the API but if you wanted to manually call the API you 
can do that as follows but first, you will need the integration testing resources defined above installed 
and configured on your machine for each of the scenarios defined below.

- Integrate your IDE with Jetty - 
  Once installed, configure the web app folder to this projects WAR file folder of target/ServerWARAppTemplate.
  With Jetty integrated into your IDE, I use Eclipse, you can start Jetty by right clicking on the POM file and 
  "Run As" "Run with Jetty". Jetty will load the generated WAR file and you will be up and running.
- Download Jetty Runner -
  The idea of the jetty-runner is extremely simple – run a webapp directly from the command line using a 
  single jar file and as much default configuration as possible. 
  java -jar jetty-runner-9.4.9.v20180320.jar ServerWARAppTemplate.war
- Install the WAR file in a standalone web server like Apache Tomcat.   

The API can be called by command line or with the use of a tool like Postman.

There are 2 users, the admin user with a user name and password of "admin" and the regular user with the
user name and password of "user". Since the API uses the basic auth the user name and password need to
be populated in the authorization header in the request.

The "admin" user has authorization to POST, PUT, DELETE and GET.
The "user" user has authorization to only GET.

The "Accept" header, the format you want the data returned to you as, should be populated with either "application/json" or "application/xml"
The "Content-Type" header, the format you are sending, should be populated with either "application/json" or "application/xml"

To retrieve all customers call, don't forget to populate the "Accept" header as well, if not the default is json.
GET http://localhost:8080/rest/customers

To retrieve a single customer call, don't forget to populate the "Accept" header as well, if not the default is json.
GET http://localhost:8080/rest/customers/1

To create a customer call, don't forget to populate the "Content-Type" header with the format you are sending.
Here the "Content-Type" will be "application/json". The "Accept" header will be "application/json", it can be "application/xml" as well. 
POST http://localhost:8080/rest/customers

{
   "first_name": "Jack",
   "last_name": "Johnson",
   "email": "jack_johnson@yahoo.com"
}
You should receive a response with the new customer added and their {id} which you will use in the PUT and DELETE below.

To update a customer call, don't forget to populate the "Content-Type" header with the format you are sending.
Here we are updating customer with {id} that was returned to you in the POST, you need to send that customer in the body.
PUT http://localhost:8080/rest/customers/{id}

{
    "id": {id}, // optional, will be overwritten with the id in the URL
    "first_name": "Jacky",
    "last_name": "John",
    "email": "jacky_john@yahoo.com"
}

To delete a customer call
Here we are deleting the customer with {id} that you added and updated above.
DELETE http://localhost:8080/rest/customers/{id}


--- Validation ---
If the first name, last name or the email address is missing on a POST or PUT, a validation exception occurs which you should see.
The email address is unique. If the email address already exists for a customer then a validation exception occurs which you should see. 


  
  
  