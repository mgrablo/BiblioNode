package io.github.mgrablo.BiblioNode.bootstrap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class RsaKeyGenerator {

	@Value("${app.rsa.private-key}")
	private String privateKeyPath;

	@Value("${app.rsa.public-key}")
	private String publicKeyPath;

	@PostConstruct
	public void init() throws Exception {
		Path privPath = Paths.get(privateKeyPath.replace("file:", ""));
		Path pubPath = Paths.get(publicKeyPath.replace("file:", ""));

		if (Files.notExists(privPath)) {
			Files.createDirectories(privPath.getParent());

			KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(2048);
			KeyPair keyPair = generator.generateKeyPair();

			try (FileWriter writer = new FileWriter(privPath.toFile())) {
				writer.write("-----BEGIN RSA PRIVATE KEY-----\n");
				writer.write(Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
				writer.write("\n-----END RSA PRIVATE KEY-----");
			}

			try (FileWriter writer = new FileWriter(pubPath.toFile())) {
				writer.write("-----BEGIN PUBLIC KEY-----\n");
				writer.write(Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
				writer.write("\n-----END PUBLIC KEY-----");
			}

			log.info("RSA keys generated automatically for development.");
		}
	}
}
