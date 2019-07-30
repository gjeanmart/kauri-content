package io.kauri.tutorial.springboot.simple.controllers;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.elastic.apm.api.CaptureTransaction;
import io.kauri.tutorial.springboot.simple.model.Message;
import io.kauri.tutorial.springboot.simple.services.MessageService;

@RestController
public class MessageController {

	@Autowired
	private MessageService service;
	
    @GetMapping("/message")
    @CaptureTransaction(type = "request", value = "getMessages")
    public List<Message> getMessages(
    		@RequestParam(name = "error", defaultValue = "false") boolean error,
    		@RequestParam(name = "sleep", required = false) Optional<Integer> sleep) {
        return service.getMessages(error, sleep);
    }
    
    @PostMapping("/message")
    @CaptureTransaction(type = "request", value = "postMessage")
    public Message postMessage(@RequestBody String body, 
    		@RequestParam(name = "error", defaultValue = "false") boolean error,
    		@RequestParam(name = "sleep", required = false) Optional<Integer> sleep) {
        return service.postMessage(body, error, sleep);
    }
    
}
