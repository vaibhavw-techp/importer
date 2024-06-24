package com.demo.importer;

import com.demo.importer.service.AwsSecretsPropertiesListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ImporterApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(ImporterApplication.class);
		application.addListeners(new AwsSecretsPropertiesListener());
		application.run(args);
	}

}
