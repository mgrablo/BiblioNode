package io.github.mgrablo.BiblioNode.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(
		int jwtExpirationHours
) { }
