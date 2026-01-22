package com.example.joinUs;

import io.netty.util.HashingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.Security;

@SpringBootApplication
@EnableAutoConfiguration
public class JoinUsApplication {

	public static void main(String[] args) {

		SpringApplication.run(JoinUsApplication.class, args);
	}

}
