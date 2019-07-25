package io.kauri.tutorial.springboot.simple.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import io.kauri.tutorial.springboot.simple.model.Message;

public interface MessageRepository extends MongoRepository<Message, String> {

}
