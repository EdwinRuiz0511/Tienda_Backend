package com.tienda.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class TiendaBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(TiendaBackendApplication.class, args);
	}

}
