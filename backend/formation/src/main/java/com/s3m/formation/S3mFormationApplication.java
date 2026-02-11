package com.s3m.formation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class S3mFormationApplication {

	public static void main(String[] args) {
		SpringApplication.run(S3mFormationApplication.class, args);
	}

}
