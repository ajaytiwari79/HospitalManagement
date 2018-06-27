package com.kairos.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class KairosRegisteryApplication {

	public static void main(String[] args) {
		SpringApplication.run(KairosRegisteryApplication.class, args);
	}
}
