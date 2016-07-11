#Build a Spring Boot Application with JPA

In this exercise you'll build a basic Spring Boot application that uses JPA access a database. When run locally it will use an in memory instance of HSQLDB, but when pushed to Cloud Foundry and bound with a MySQL instance it will "auto-magically" use it instead.

You'll start with a shell project, create a Domain, add an Interface which will tell Spring Data to create a Repository, and then add a bit of code to initialize it with some data.  After that you'll be able to start it as a Web application and browse the data via a ReSTful API.  With that done, you will push it to Cloud Fundry and bind to a MySQL database instance.

##1 Create a new project using the Spring Initializer

1. If it's not running already launch SpringSource Tool Suite (STS)
2. File -> New -> Spring Starter Project
3. Enter a unique name and artifact-id.  You can also enter other information as you like (group-id, package, etc.).  Click Next when done.
4. Add feature to the application by checking: **Web**, **JPA**, **Rest Repositories**, **HSQLDB**, and **MySQL**.  Click Finish when done, and this will create and load the new project into STS.  Use the search box to find them if they're not listed at the top.

![starter1][./img/starter1.png=100x]
![alt text][starter2]

If you're not familiar with Spring Boot apps spend a little time exploring the project.  There's a "main" class that tells Spring to start up and initialize everything, an applications.properties that's a default location for key/value pairs, and the POM is setup with dependancies that will tell Spring Boot to do things for us.  For example, adding the Web starter tells Boot to embed a Tomcat server in our app and setup its context so it just works.

##2 Add Domain Object

First create a basic class to model a domain.  This will be nothing more then a string with an id.

1. Right click on the package under src/main/java and select New -> Class
2. Enter Greeting for the class name, and click Finish
3. In the new class add the following code:

```java
package io.pivotal.demo; //don't copy/paste this unless is matches your packge name

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Greeting {

  @Id
  private Integer id;
  private String text;
    
  public Greeting(Integer id, String text) {
    super();
    this.id = id;
    this.text = text;
  }

  @Override
  public String toString() {
    return "Greeting [id=" + id + ", text=" + text + "]";
  }

  public Integer getId() {
    return id;
  }

  public String getText() {
    return text;
  }

  public Greeting() {}
}
```

##3 Add Repository

Next, create an Interface that will tell Spring Data that you want to setup a Repository to manage our new Domain class.  The empty repository definition will create it with only basic operations.

1. Right click on the package under src/main/java and select New -> Interface
2. Enter GreetingRepository for the name, and click Finish
3. In the new interface add the following code:

```java
package io.pivotal.demo; //don't copy/paste this unless is matches your packge name

import org.springframework.data.jpa.repository.JpaRepository;

public interface GreetingRepository extends JpaRepository<Greeting, Integer> {
 
}
```
(Yes, it really is this easy to define a Repository!)

##4 Setup the DB with Initial Data

In this step you will create a Configuration class that will generate a Bean of type CommandLineRunner.  Instances of this class will be run by Spring Boot when it starts up.  We'll use Spring's dependancy injection to pass in our Spring Data created Repository, and then populate it with some data.  A little bit of Java 8 lambda goodness will make it look pretty.

1. Right click on the package under src/main/java and select New -> Class
2. Enter GreetingConfig for the name, and click Finish
3. In the new class add the following code:

```java
package io.pivotal.demo; //don't copy/paste this unless is matches your packge name

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GreetingConfig {

  Logger logger = LoggerFactory.getLogger(GreetingConfig.class);

  // Loads the database on startup
  @Bean
  CommandLineRunner loadDatabase(GreetingRepository gr) {
    return commandLineRunner -> {
      logger.debug("loading database..");
      gr.save(new Greeting(1, "Hello"));
      gr.save(new Greeting(2, "Hola"));
      gr.save(new Greeting(3, "Ohai"));
      logger.debug("record count: {}", gr.count());
      gr.findAll().forEach(x -> logger.debug(x.toString()));
    };
  }
}
```

##5 Set Default Properties

1. Rename the file src/main/resources/application.properties to application.yml (select the file, and use the menu Refactor -> Rename...)
2. Add the following properties

```properties
logging:
  level:
    io:
      pivotal: DEBUG
spring:
  jpa:
    hibernate:
      ddl-auto: create-drop
```

The first property sets the logging level for everything under the package io.pivotal to DEBUG.  If you named you package something different change the properties to reflect the proper name.  This will allow you to see your logging messages in the Configuration class.

The second property is a Hibernate specific setting.  This will create a schema in the DB to support our applicaiton (destroying any existing version), and when the app closes the Session the schema will be deleted.  This is good for demos where you want to keep your DB clean.  (See the hibernate documentation on more options for this setting.)

##6 Browse the Data

With all that done, launch the app and browse the data!

1. From the Boot Dashboard on STS select the application you created and click the start/re-start button (the one with the red square and green arrow on it).
2. Switch to or launch a browser and go to the URL: http://localhost:8080/greetings

![alt text][boot-dashboard]

##7 Add a Search Method

Now add a method to the Repository to do some searching.

1.  Go to the GreetingRepository class and add the following imports/method:

```java
import java.util.List;
import org.springframework.data.repository.query.Param;

//In the Interface
  List<Greeting> findByText(@Param("text") String text);  
```

1. Go to the GreetingConfig class and add the following to the part where you create records:

```java
      gr.save(new Greeting(4, "Hello"));
```

Restart the application and browse to the URL: http://localhost:8080/greetings/search

Notice the format that it gives you to search.  You can now find the two records you entered that have the value Hello with a URL like this:

http://localhost:8080/greetings/search/findByText?text=Hello

##8 Push to Cloud Foundry

TBD


[starter1]: https://github.com/Pivotal-Field-Engineering/spring-boot-data-rest-demo/blob/master/img/starter1.png "Starter Page 1"
[starter2]: https://github.com/Pivotal-Field-Engineering/spring-boot-data-rest-demo/blob/master/img/starter2.png "Starter Page 2"
[boot-dashboard]: https://github.com/Pivotal-Field-Engineering/spring-boot-data-rest-demo/blob/master/img/boot-dashboard.png "Boot Dashboard"