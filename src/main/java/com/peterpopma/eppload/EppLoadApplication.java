package com.peterpopma.eppload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class EppLoadApplication {

	public static void main(String[] args) {
		SpringApplication.run(EppLoadApplication.class, args);
	}

}
