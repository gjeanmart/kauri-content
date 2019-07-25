package io.kauri.tutorial.springboot.simple.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class MainController {

    @GetMapping("/")
    public String greetings() {
        return "Greetings from Spring Boot!";
    }
    
    @GetMapping("/okOrError")
    public String okOrError(@RequestParam(name = "status", defaultValue = "ok") String status) {
        if(status.equalsIgnoreCase("ok")) {
        	return "ok";
        } else {
        	throw new RuntimeException("ERRRRRORRRRR");
        }
    }

}
