package com.gymtracker.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = { UserDetailsServiceAutoConfiguration.class })
public class GymTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(GymTrackerApplication.class, args);
	}

}
