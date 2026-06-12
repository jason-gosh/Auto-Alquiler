package com.json.AutoAlquiler;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableWebSecurity
public class AutoAlquilerApplication {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.systemDefault()));
		System.out.println(LocalDate.now());
    }

	public static void main(String[] args) {
		SpringApplication.run(AutoAlquilerApplication.class, args);
	}

}
