package com.outgo.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OutgoApplication {

	public static void main(String[] args) {
		SpringApplication.run(OutgoApplication.class, args);
	}

}
