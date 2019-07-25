package io.kauri.tutorial.springboot.simple.controllers;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.kauri.tutorial.springboot.simple.model.Message;
import io.kauri.tutorial.springboot.simple.repository.MessageRepository;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class MessageController {

	@Autowired
	private MessageRepository repository;
	
    @GetMapping("/message")
    public List<Message> getMessages() {
    	log.info("getting all messages");
        return repository.findAll();
    }
    
    @PostMapping("/message")
    public Message postMessage(@RequestBody String body) {
    	Message message = new Message();
    	message.setMessage(body);
    	message.setPostedAt(new Date());
    	
    	log.info("posting {}", message);
    	
        return repository.save(message);
    }
    
}
