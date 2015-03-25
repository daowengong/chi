package com.llr.chihuo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@ComponentScan
public class ApplicationMain {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(ApplicationMain.class, args);
	}
}
