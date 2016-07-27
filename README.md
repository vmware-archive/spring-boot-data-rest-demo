#Build a Spring Boot Application with JPA

In this exercise you'll build a basic Spring Boot application that uses JPA access a database. When run locally it will use an in memory instance of HSQLDB, but when pushed to Cloud Foundry and bound with a MySQL instance it will "auto-magically" use it instead.

You'll start with a shell project, create a Domain, add an Interface which will tell Spring Data to create a Repository, and then add a bit of code to initialize it with some data.  After that you'll be able to start it as a Web application and browse the data via a ReSTful API.  With that done, you will push it to Cloud Fundry and bind to a MySQL database instance.

##1 Create a new project using the Spring Initializer

1. If it's not running already launch SpringSource Tool Suite (STS)
2. File -> New -> Spring Starter Project
3. Enter a unique name and artifact-id.  You can also enter other information as you like (group-id, package, etc.).  Click Next when done.
4. Add feature to the application by checking: **Web**, **JPA**, **Rest Repositories**, **HSQLDB**, **MySQL**, and **Actuator**.  Click Finish when done, and this will create and load the new project into STS.  Use the search box to find them if they're not listed at the top.

![starter1](./img/starter1.png)
![starter2](./img/starter2.png)

If you're not familiar with Spring Boot apps spend a little time exploring the project.  There's a "main" class that tells Spring to start up and initialize everything, an applications.properties that's a default location for key/value pairs, and the POM is setup with dependancies that will tell Spring Boot to do things for us.  For example, adding the Web starter tells Boot to embed a Tomcat server in our app and setup its context so it just works.

##2 Add Domain Object

First create a basic class to model a domain.  This will be nothing more then a string with an id.

1. Right click on the package under src/main/java and select New -> Class
2. Enter Greeting for the class name, and click Finish
3. In the new class add the following code:

```java
package io.pivotal.demo;  //don't copy/paste this unless is matches your packge name

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Greeting {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;
  private String text;

  public Greeting(String text) {
    super();
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
      gr.save(new Greeting("Hello"));
      gr.save(new Greeting("Hola"));
      gr.save(new Greeting("Ohai"));
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

Spring Boot use a convention where it loads applications.yml (or .properties) by default.  The first property sets the logging level for everything under the package io.pivotal to DEBUG.  If you named you package something different change the properties to reflect the proper name.  This will allow you to see your logging messages in the Configuration class.

The second property is a Hibernate specific setting.  This will create a schema in the DB to support our applicaiton (destroying any existing version), and when the app closes the Session the schema will be deleted.  This is good for demos where you want to keep your DB clean.  (See the hibernate documentation on more options for this setting.)

##6 Browse the Data

With all that done, launch the app and browse the data!

1. From the Boot Dashboard on STS select the application you created and click the start/re-start button (the one with the red square and green arrow on it).
2. Switch to or launch a browser and go to the URL: http://localhost:8080/greetings

![alt text](./img/boot-dashboard.png)

###6.1 Check out the Actuator

Spring Boot brought us Actuator which generates a set of endpoints that provides information about what's happening in your application.  Take a minute and look at all the good things you have access to view.

http://localhost:8080/env
http://localhost:8080/beans


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
      gr.save(new Greeting("Hello"));
```

Restart the application and browse to the URL: http://localhost:8080/greetings/search

Notice the format that it gives you to search.  You can now find the two records you entered that have the value Hello with a URL like this:

http://localhost:8080/greetings/search/findByText?text=Hello

##8 Push to Cloud Foundry

In this step you will build the application into a self-executing jar file and deploy it onto the Pivotal WebServices instance of Cloud Foundry using the command line tool ```cf```.  The tool is already installed on the demo machine, and logged in to an account.  For more information on all this, check out the demo and talk with the technical team at the PCF stations.

1. Go to the terminal, and navigate to your application.
2. Build the application with maven.
3. Push the applicaiton to Cloud Foundry

```bash
$ cd ~/S1P2016/workspace/<your_project>
$ ./mvnw clean package
(lots of output from build)
$ cf push your_app_name -p target/your_app_name-0.0.1-SNAPSHOT.jar --random-route
(lots of output from the push)
```

The --random-route flag tells Cloud Fundry to add random words to the URL for your app so that it won't conflict with any other applications that might use the same name you did.

## Add a Fancy UI

This is an optional step if you want to explore what a JavaScript consumer would look like.

If you're intereted click the link: [Add UI](AddUI.md)

## Cleanup

So the environment is ready for the next person, it would be helpful if you deleted files.  This keeps the project explorer clean, and if the someone happens to use the same name as you did there won't be any conflicts.

Thanks!

1. Delete the app from Cloud Foundry

```bash
$ cf delete your_app_name
Really delete the app your_app_name?> y
```

2. Right click on your project and select delete.  Click yes to delete the files.

# We hope you enjoyed the demo! :-)
