package com.example.illook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
public class IllookApplication {

	public static void main(String[] args) {
		SpringApplication.run(IllookApplication.class, args);
	}

}
