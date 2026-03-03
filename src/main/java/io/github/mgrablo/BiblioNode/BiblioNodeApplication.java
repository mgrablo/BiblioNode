package io.github.mgrablo.BiblioNode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import io.github.mgrablo.BiblioNode.config.LoanProperties;
import io.github.mgrablo.BiblioNode.config.SecurityProperties;

@SpringBootApplication
@EnableConfigurationProperties({LoanProperties.class, SecurityProperties.class})
public class BiblioNodeApplication {

	public static void main(String[] args) {
		SpringApplication.run(BiblioNodeApplication.class, args);
	}

}
