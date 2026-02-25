package io.github.mgrablo.BiblioNode.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableConfigurationProperties(RsaKeyProperties.class)
@Slf4j
public class RsaKeyConfig {
	private final RsaKeyProperties rsaKeyProperties;


	public RsaKeyConfig(RsaKeyProperties rsaKeyProperties, Environment environment) {
		this.rsaKeyProperties = rsaKeyProperties;

		List<String> activeProfiles = Arrays.asList(environment.getActiveProfiles());

		if (activeProfiles.contains("dev") || activeProfiles.contains("test")) {
			ensureKeysExist();
		}
	}

	public RSAPublicKey getPublicKey() {
		try {
			String key = readKey(rsaKeyProperties.getPublicKey());
			byte[] encoded = Base64.getMimeDecoder().decode(key);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(encoded));
		} catch (Exception e) {
			throw new RuntimeException("Could not load public key", e);
		}
	}

	public RSAPrivateKey getPrivateKey() {
		try {
			String key = readKey(rsaKeyProperties.getPrivateKey());
			byte[] encoded = Base64.getMimeDecoder().decode(key);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return (RSAPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encoded));
		} catch (Exception e) {
			throw new RuntimeException("Could not load private key", e);
		}
	}

	private void ensureKeysExist() {
		try {
			String privStr = rsaKeyProperties.getPrivateKey().replace("file:", "");
			String pubStr = rsaKeyProperties.getPublicKey().replace("file:", "");
			Path privPath = Paths.get(privStr);
			Path pubPath = Paths.get(pubStr);

			if (Files.notExists(privPath)) {
				log.info("Generating RSA keys for dev profile...");

				Files.createDirectories(privPath.getParent());
				KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
				generator.initialize(2048);
				KeyPair keyPair = generator.generateKeyPair();

				saveKey(privPath, "PRIVATE", keyPair.getPrivate().getEncoded());
				saveKey(pubPath, "PUBLIC", keyPair.getPublic().getEncoded());
			}
		} catch (Exception e) {
			throw new RuntimeException("Could not initialize RSA keys", e);
		}
	}

	private void saveKey(Path path, String type, byte[] encoded) throws IOException {
		String content = Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(encoded);
		try (FileWriter writer = new FileWriter(path.toFile())) {
			writer.write("-----BEGIN " + type + " KEY-----\n");
			writer.write(content);
			writer.write("\n-----END " + type + " KEY-----\n");
		}
	}

	private String readKey(String path) throws IOException {
		return Files.readString(Paths.get(path.replace("file:", "")))
				.replace("-----BEGIN RSA PRIVATE KEY-----", "")
				.replace("-----END RSA PRIVATE KEY-----", "")
				.replace("-----BEGIN PUBLIC KEY-----", "")
				.replace("-----END PUBLIC KEY-----", "")
				.replace("-----BEGIN PRIVATE KEY-----", "")
				.replace("-----END PRIVATE KEY-----", "")
				.replaceAll("\\s", "");
	}
}
