package com.cg;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@SpringBootApplication
@SecurityScheme(
	    name = "BearerAuth",
	    type = SecuritySchemeType.HTTP,
	    bearerFormat = "JWT",
	    scheme = "bearer",
	    in = SecuritySchemeIn.HEADER
	)
@OpenAPIDefinition(
	    security = @SecurityRequirement(name = "BearerAuth")  
	)
public class DigitalGoldWalletApplication {

	public static void main(String[] args) {
		SpringApplication.run(DigitalGoldWalletApplication.class, args);
	}

}
