package io.github.mgrablo.BiblioNode.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "app.rsa")
@Getter @Setter
public class RsaKeyProperties {
	private String privateKey;
	private String publicKey;
}
