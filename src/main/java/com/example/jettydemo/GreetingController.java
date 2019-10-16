package com.example.jettydemo;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();

	// Sample Request http://localhost:8080/greeting?name=Anees
	// Sample Response {"id":2,"content":"Hello, Anees!"}
	@RequestMapping("/greeting")
	public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) throws IOException {
//		String response = String.join("", Files.readAllLines(Paths.get("src/main/response.txt").toAbsolutePath()));
//		System.out.println(response+"\n");
//		return new ResponseEntity<String>(response, HttpStatus.OK);
		Greeting greeting = new Greeting(counter.getAndIncrement(), name);
		return greeting;
	}
}
