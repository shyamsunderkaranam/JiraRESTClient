package com.jira.jirarestclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class JirarestclientApplication  extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(JirarestclientApplication.class, args);
	}

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(JirarestclientApplication.class);
    }
    
    
}
