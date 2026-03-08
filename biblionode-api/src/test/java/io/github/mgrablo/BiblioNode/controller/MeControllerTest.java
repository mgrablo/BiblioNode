package io.github.mgrablo.BiblioNode.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import io.github.mgrablo.BiblioNode.config.RsaKeyConfig;
import io.github.mgrablo.BiblioNode.config.SecurityConfiguration;
import io.github.mgrablo.BiblioNode.dto.LoanResponse;
import io.github.mgrablo.BiblioNode.dto.UserProfileResponse;
import io.github.mgrablo.BiblioNode.exception.GlobalExceptionHandler;
import io.github.mgrablo.BiblioNode.service.LoanService;
import io.github.mgrablo.BiblioNode.service.ReaderService;

@WebMvcTest(MeController.class)
@Import({SecurityConfiguration.class, GlobalExceptionHandler.class})
public class MeControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ReaderService readerService;

	@MockitoBean
	private LoanService loanService;

	@MockitoBean
	private JwtDecoder jwtDecoder;
	@MockitoBean
	private JwtEncoder jwtEncoder;
	@MockitoBean
	private RsaKeyConfig rsaKeyConfig;

	@Test
	public void getProfile_ShouldReturnProfile_WhenAuthenticated() throws Exception {
		String email = "test@email.com";
		UserProfileResponse response = new UserProfileResponse(
				email,
				"Test Name",
				LocalDateTime.now(),
				2L
		);

		when(readerService.getUserProfileByEmail(email)).thenReturn(response);

		mockMvc.perform(get("/api/me")
						.with(readerUser(email))
						.contentType(MediaType.APPLICATION_JSON)
				).andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value(email))
				.andExpect(jsonPath("$.name").value("Test Name"))
				.andExpect(jsonPath("$.activeLoansCount").value(2));
	}

	@Test
	public void getProfile_ShouldReturnUnauthorized_WhenNotAuthenticated() throws Exception {
		mockMvc.perform(get("/api/me")
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isUnauthorized());
	}

	@Test
	public void getMyLoans_ShouldReturnLoans_WhenAuthenticated() throws Exception {
		String email = "test@email.com";
		LoanResponse loan1 = createTestLoanResponse(1L, "Book One");
		LoanResponse loan2 = createTestLoanResponse(2L, "Book Two");
		Page<LoanResponse> loansPage = new PageImpl<>(List.of(loan1, loan2));

		when(loanService.getLoansByReaderEmail(anyString(), any(Pageable.class))).thenReturn(loansPage);

		mockMvc.perform(get("/api/me/loans")
						.with(readerUser(email))
						.contentType(MediaType.APPLICATION_JSON)
				).andExpect(status().isOk())
				.andExpect(jsonPath("$.content.length()").value(2))
				.andExpect(jsonPath("$.content[0].id").value(1L))
				.andExpect(jsonPath("$.content[0].bookTitle").value("Book One"))
				.andExpect(jsonPath("$.content[1].id").value(2L))
				.andExpect(jsonPath("$.content[1].bookTitle").value("Book Two"));

	}

	@Test
	public void getMyLoans_ShouldReturnUnauthorized_WhenNotAuthenticated() throws Exception {
		mockMvc.perform(get("/api/me/loans")
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isUnauthorized());
	}

	private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor readerUser(String email) {
		return jwt().authorities(new SimpleGrantedAuthority("ROLE_READER"))
				.jwt(j -> j.subject(email));
	}

	private LoanResponse createTestLoanResponse(Long id, String bookTitle) {
		return new LoanResponse(
				id,
				1L,
				bookTitle,
				"Author Name",
				"ISBN1234567890",
				2L,
				LocalDateTime.now(),
				null,
				null
		);
	}
}
