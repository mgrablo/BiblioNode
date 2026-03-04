package io.github.mgrablo.BiblioNode.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.mgrablo.BiblioNode.exception.GlobalExceptionHandler;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({SecurityConfiguration.class, GlobalExceptionHandler.class})
public class PaginationIntegrationTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private PaginationProperties paginationProperties;

	@MockitoBean
	private JwtDecoder jwtDecoder;
	@MockitoBean
	private JwtEncoder jwtEncoder;
	@MockitoBean
	private RsaKeyConfig rsaKeyConfig;

	@Test
	void shouldApplyDefaultPageSize() throws Exception {
		int expectedPageSize = paginationProperties.defaultPageSize();
		mockMvc.perform(get("/api/books")
						.with(readerUser())
				).andExpect(status().isOk())
				.andExpect(jsonPath("$.pageable.pageSize").value(expectedPageSize))
				.andExpect(jsonPath("$.size").value(expectedPageSize));
	}

	@Test
	void shouldNotExceedMaxPageSize() throws Exception {
		int maxPageSize = paginationProperties.maxPageSize();

		mockMvc.perform(get("/api/books").with(readerUser())
						.param("size", "10000")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.pageable.pageSize").value(maxPageSize))
				.andExpect(jsonPath("$.size").value(maxPageSize));
	}


	private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor readerUser() {
		return jwt().authorities(new SimpleGrantedAuthority("ROLE_READER"))
				.jwt(j -> j.subject("test@email.com"));
	}
}
