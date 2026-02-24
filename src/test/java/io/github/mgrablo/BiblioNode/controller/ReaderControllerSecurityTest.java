package io.github.mgrablo.BiblioNode.controller;

import io.github.mgrablo.BiblioNode.config.RsaKeyConfig;
import io.github.mgrablo.BiblioNode.config.SecurityConfiguration;
import io.github.mgrablo.BiblioNode.dto.ReaderRequest;
import io.github.mgrablo.BiblioNode.dto.ReaderResponse;
import io.github.mgrablo.BiblioNode.service.ReaderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReaderController.class)
@AutoConfigureMockMvc
@Import(SecurityConfiguration.class)
public class ReaderControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReaderService readerService;

    @MockitoBean
    private RsaKeyConfig rsaKeyConfig;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private JwtEncoder jwtEncoder;

    @Test
    void getReaderById_ShouldAllowAdmin() throws Exception {
        when(readerService.getReaderById(1L))
                .thenReturn(new ReaderResponse(1L, "Full Name", "email@example.com", Collections.emptyList()));

        mockMvc.perform(get("/api/readers/1")
                .with(user("admin").roles("ADMIN"))
        ).andExpect(status().isOk());
    }

    @Test
    void getReaderById_ShouldForbidReader() throws Exception {
        mockMvc.perform(get("/api/readers/1")
                .with(user("reader").roles("READER"))
        ).andExpect(status().isForbidden());
    }

    @Test
    void getAll_ShouldAllowAdmin() throws Exception {
        when(readerService.getAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/api/readers")
                .with(user("admin").roles("ADMIN"))
        ).andExpect(status().isOk());
    }

    @Test
    void getAll_ShouldForbidReader() throws Exception {
        mockMvc.perform(get("/api/readers")
                .with(user("reader").roles("READER"))
        ).andExpect(status().isForbidden());
    }

    @Test
    void updateReader_ShouldAllowAdmin() throws Exception {
        when(readerService.updateReader(eq(1L), any(ReaderRequest.class)))
                .thenReturn(new ReaderResponse(1L, "Updated Name", "email@example.com", Collections.emptyList()));

        mockMvc.perform(put("/api/readers/1")
                .with(user("admin").roles("ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"fullName\":\"Updated Name\"}")
        ).andExpect(status().isOk());
    }

    @Test
    void updateReader_ShouldForbidReader() throws Exception {
        mockMvc.perform(put("/api/readers/1")
                .with(user("reader").roles("READER"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"fullName\":\"Updated Name\"}")
        ).andExpect(status().isForbidden());
    }

    @Test
    void deleteReader_ShouldAllowAdmin() throws Exception {
        mockMvc.perform(delete("/api/readers/1")
                .with(user("admin").roles("ADMIN"))
                .with(csrf())
        ).andExpect(status().isNoContent());
    }

    @Test
    void deleteReader_ShouldForbidReader() throws Exception {
        mockMvc.perform(delete("/api/readers/1")
                .with(user("reader").roles("READER"))
                .with(csrf())
        ).andExpect(status().isForbidden());
    }

    @Test
    void getReaderByEmail_ShouldAllowAdmin() throws Exception {
        when(readerService.getReaderByEmail(any(String.class)))
                .thenReturn(new ReaderResponse(1L, "Full Name", "email@example.com", Collections.emptyList()));

        mockMvc.perform(get("/api/readers/email")
                .param("email", "email@example.com")
                .with(user("admin").roles("ADMIN"))
        ).andExpect(status().isOk());
    }

    @Test
    void getReaderByEmail_ShouldForbidReader() throws Exception {
        mockMvc.perform(get("/api/readers/email")
                .param("email", "email@example.com")
                .with(user("reader").roles("READER"))
        ).andExpect(status().isForbidden());
    }
}

