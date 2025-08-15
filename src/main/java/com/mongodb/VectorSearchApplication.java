package com.mongodb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class VectorSearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(VectorSearchApplication.class, args);
	}

}
