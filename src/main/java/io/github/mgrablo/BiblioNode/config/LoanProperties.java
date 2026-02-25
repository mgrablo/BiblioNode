package io.github.mgrablo.BiblioNode.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "app.loan")
@Getter @Setter
public class LoanProperties {
	private int maxActiveLoans = 5;
	private int defaultLoanDays = 14;
}