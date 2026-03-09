package io.github.mgrablo.BiblioNode.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(
		int jwtExpirationHours,
		CorsProperties cors
) {
	public record CorsProperties(
			List<String> allowedOrigins,
			List<String> allowedMethods,
			List<String> allowedHeaders,
			boolean allowCredentials
	) { }
}
