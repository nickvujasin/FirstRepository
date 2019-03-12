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

Unit testing uses an embedded H2 database along with an embedded Artemis MOM.

DBUnit is used to provide the embedded H2 database in the same state between test methods.

Liquibase is used to provided database source control. It creates the tables in the embedded H2 database. 


--- Integration Testing ---
Integration testing uses a Jetty HTTP Server, a standalone MySql database along with a standalone Artemis MOM.

HttpClient and Jersey's JAX-RS HTTP client are used to test the REST API all the way through to the database.

DBUnit is used to provide the standalone MySql database in the same state between test methods. It is also used
to backup the database, configure the test data and after the tests are run set the database back to its pre-test state.

Liquibase is used to provided database source control. It creates the tables in the standalone MySql database if they do not exist.


--- Integration Testing Resources ---
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
6. To run just the integration tests: mvn verify -Pskip.unit.tests
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