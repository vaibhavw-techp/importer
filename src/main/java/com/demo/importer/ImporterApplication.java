package com.demo.importer;


import com.demo.importer.service.DbPropertiesListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
public class ImporterApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImporterApplication.class, args);
		app.addListeners(new DbPropertiesListener());
		app.run(args);
	}

}
