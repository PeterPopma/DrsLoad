package com.peterpopma.drsload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DrsLoadApplication {

	public static void main(String[] args) {
		SpringApplication.run(DrsLoadApplication.class, args);
	}

}
