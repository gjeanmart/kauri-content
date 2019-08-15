package io.kauri.tutorial.springboot.simple.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.kauri.tutorial.springboot.simple.model.Message;
import io.kauri.tutorial.springboot.simple.repository.MessageRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MessageService {

	@Autowired
	private MessageRepository repository;
	
    @Scheduled(fixedDelayString = "60000")
    public void execute() {
        log.info("run scheduled test");
        doExecute();
    }
    
    private void doExecute() {
    	repository.findAll().forEach(msg-> log.debug(msg.getMessage()));
    }
	
    public List<Message> getMessages(boolean error, Optional<Integer> sleep) {
    	try {
        	log.info("getting all messages...");
        	
        	if(error)
        		throw new Exception("Random error");
        	
        	if(sleep.isPresent())
        		Thread.sleep(sleep.get());
        	
            return repository.findAll();
    		
    	} catch(Exception ex) {
    		log.error("Error {}", ex.getMessage(), ex);
    		throw new RuntimeException(ex);
    	}
    }

    public Message postMessage(String body, boolean error, Optional<Integer> sleep) {
    	
    	try {
        	Message message = new Message();
        	message.setMessage(body);
        	message.setPostedAt(new Date());
        	
        	log.info("posting {} ...", message);

        	if(error)
        		throw new Exception("Random error");
        	
        	if(sleep.isPresent())
        		Thread.sleep(sleep.get());
        	
        	message = repository.save(message);
        	
        	log.info("posted {} !", message);
        	
        	return message;
    		
    	} catch(Exception ex) {
    		log.error("Error {}", ex.getMessage(), ex);
    		throw new RuntimeException(ex);
    	}
    }
}
