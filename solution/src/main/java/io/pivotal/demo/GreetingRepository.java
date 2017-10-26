package io.pivotal.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.repository.query.Param;

public interface GreetingRepository extends JpaRepository<Greeting, Integer> {
	  List<Greeting> findByText(@Param("text") String text); 
}