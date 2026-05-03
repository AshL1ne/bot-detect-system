package com.bds;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.bds")
public class BdsApplication {

	public static void main(String[] args) {
		SpringApplication.run(BdsApplication.class, args);
	}

}
