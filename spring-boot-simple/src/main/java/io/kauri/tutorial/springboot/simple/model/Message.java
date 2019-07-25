package io.kauri.tutorial.springboot.simple.model;

import java.util.Date;

import org.springframework.data.annotation.Id;

import lombok.Data;

@Data
public class Message {

    @Id
    public String id;

    public String message;
    
    public Date postedAt;
}
