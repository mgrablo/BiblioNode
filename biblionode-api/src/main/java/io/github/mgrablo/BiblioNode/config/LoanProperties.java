package io.github.mgrablo.BiblioNode.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.loan")
public record LoanProperties(
		int maxActiveLoans,
		int defaultLoanDays
) {
}