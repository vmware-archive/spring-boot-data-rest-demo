#Build a Spring Boot Application with JPS

In this exercise you'll build a basic Spring Boot application that uses JPA access a database. When run locally it will use an in memory instance of HSQLDB, but when pushed to Cloud Foundry and bound with a MySQL instance it will "auto-magically" use it instead.

You'll start with a shell project, create a Domain, add an Interface which will tell Spring Data to create a Repository, and then add a bit of code to initialize it with some data.  After that you'll be able to start it as a Web application and browse the data via a ReSTful API.

###BONUS
Take a little more time and add a JavaScript driven page to browse the data.

##1 Create a new project using the Spring Initializer

1. If it's not running already launch SpringSource Tool Suite (STS)
2. File -> New -> Spring Starter Project
3. Enter a unique name and artifact-id.  You can also enter other information as you like (group-id, package, etc.).  Click Next when done.
4. Add feature to the application by checking: Web, JPA, HSQLDB, and MySQL.  Click Finish when done, and this will create and load the new project into STS.

If you're not familiar with Spring Boot apps spend a little time exploring the project.  There's a "main" class that tells Spring to start up and initialize everything, an applications.properties that's a default location for key/value pairs, and the POM is setup with dependancies that will tell Spring Boot to do things for us.  For example, adding the Web starter tells Boot to embed a Tomcat server in our app and setup its context so it just works.

##2 Add Domain Object

First create a basic class to model a domain.  This will be nothing more then a string with an id.

1. Right click on the package under src/main/java and select New -> Class
2. Enter Greeting for the class name, and click Finish
3. In the new class add the following code:

```
package io.pivotal.hello;

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

Next, create an Interface that will tell Spring Data that you want to setup a Repository to manage our new Domain class.  The empty repository definition will create it with only basic CRUD operations.

1. Right click on the package under src/main/java and select New -> Interface
2. Enter GreetingRepository for the name, and click Finish
3. In the new interface add the following code:

```
package io.pivotal.hello;

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

```
package io.pivotal.hello;

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

##5 Browse the Data

With all that done, launch the app and browse the data!

1. From the Boot Dashboard on STS select the application you created and click the start/re-start button (the one with the red square and green arrow on it).
2. Switch to or launch a browser and go to the URL: http://localhost:8080/greetings

##7 Add a Search Method

Now you're going to add a method to the Repository to do some searching.

TBD

##8 Push to Cloud Foundry

TBD

##7 BONUS: Add a JavaScript UI

In Progress - TBD

1. Create index.html in src/main/resources/static (default web root for Spring Boot apps)
2. Install Polymer into the app at web root (static)

```
bower init

bower install --save Polymer/polymer
bower install --save PolymerElements/iron-ajax
bower install --save PolymerElements/paper-button
```

1. Add the elements directory
2. Create message-display.html
3. Update index.html

index.html
```
<!DOCTYPE html>
<html>
  <head>
    <script src="bower_components/webcomponentsjs/webcomponents-lite.min.js"></script>
    <link rel="import" href="elements/greeting-display.html">
  </head>
  <body>
    <greeting-display></greeting-display>
  </body>
</html>
```
