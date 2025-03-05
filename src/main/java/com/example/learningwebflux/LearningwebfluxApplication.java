package com.example.learningwebflux;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(
	scanBasePackages = {
		"com.example.learningwebflux",
		"com.example.learningwebflux.notification",
		"com.example.learningwebflux.medialibrary",
		"com.example.learningwebflux.authentication",
		"com.example.learningwebflux.platformconnection",
		"com.example.learningwebflux.post",
		"com.example.learningwebflux.postanalytics"
	},
	nameGenerator = FullyQualifiedBeanNameGenerator.class,
	exclude = { R2dbcAutoConfiguration.class }
)
@EnableScheduling
public class LearningwebfluxApplication {
	public static void main(String[] args) {
		SpringApplication.run(LearningwebfluxApplication.class, args);
	}


	private static final Logger logger = LoggerFactory.getLogger(LearningwebfluxApplication.class);
}