package io.github.mgrablo.BiblioNode.controller;

import io.github.mgrablo.BiblioNode.config.RsaKeyConfig;
import io.github.mgrablo.BiblioNode.config.SecurityConfiguration;
import io.github.mgrablo.BiblioNode.dto.AuthorRequest;
import io.github.mgrablo.BiblioNode.dto.AuthorResponse;
import io.github.mgrablo.BiblioNode.service.AuthorService;
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

@WebMvcTest(AuthorController.class)
@AutoConfigureMockMvc
@Import(SecurityConfiguration.class)
public class AuthorControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private RsaKeyConfig rsaKeyConfig;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private JwtEncoder jwtEncoder;

    @Test
    void addAuthor_ShouldAllowAdmin() throws Exception {
        when(authorService.saveAuthor(any(AuthorRequest.class)))
                .thenReturn(new AuthorResponse(1L, "Author Name", "Bio", Collections.emptyList(), java.time.LocalDateTime.now(), java.time.LocalDateTime.now()));

        mockMvc.perform(post("/api/authors")
                .with(user("admin").roles("ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Author Name\",\"biography\":\"Bio\"}")
        ).andExpect(status().isCreated());
    }

    @Test
    void addAuthor_ShouldForbidReader() throws Exception {
        mockMvc.perform(post("/api/authors")
                .with(user("reader").roles("READER"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Author Name\",\"biography\":\"Bio\"}")
        ).andExpect(status().isForbidden());
    }

    @Test
    void getAll_ShouldAllowReader() throws Exception {
        when(authorService.getAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/api/authors")
                .with(user("reader").roles("READER"))
        ).andExpect(status().isOk());
    }

    @Test
    void getAll_ShouldAllowAdmin() throws Exception {
        when(authorService.getAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/api/authors")
                .with(user("admin").roles("ADMIN"))
        ).andExpect(status().isOk());
    }

    @Test
    void getById_ShouldAllowReader() throws Exception {
        when(authorService.findById(1L))
                .thenReturn(new AuthorResponse(1L, "Author Name", "Bio", Collections.emptyList(), java.time.LocalDateTime.now(), java.time.LocalDateTime.now()));

        mockMvc.perform(get("/api/authors/1")
                .with(user("reader").roles("READER"))
        ).andExpect(status().isOk());
    }

    @Test
    void updateAuthor_ShouldAllowAdmin() throws Exception {
        when(authorService.updateAuthor(eq(1L), any(AuthorRequest.class)))
                .thenReturn(new AuthorResponse(1L, "Updated Name", "Bio", Collections.emptyList(), java.time.LocalDateTime.now(), java.time.LocalDateTime.now()));

        mockMvc.perform(put("/api/authors/1")
                .with(user("admin").roles("ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Updated Name\",\"biography\":\"Bio\"}")
        ).andExpect(status().isOk());
    }

    @Test
    void updateAuthor_ShouldForbidReader() throws Exception {
        mockMvc.perform(put("/api/authors/1")
                .with(user("reader").roles("READER"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Updated Name\",\"biography\":\"Bio\"}")
        ).andExpect(status().isForbidden());
    }

    @Test
    void unauthenticated_ShouldBeUnauthorized() throws Exception {
        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void searchByName_ShouldAllowReader() throws Exception {
        when(authorService.findByName(any(String.class)))
                .thenReturn(new AuthorResponse(1L, "Author Name", "Bio", Collections.emptyList(), java.time.LocalDateTime.now(), java.time.LocalDateTime.now()));

        mockMvc.perform(get("/api/authors/search")
                .param("name", "Author Name")
                .with(user("reader").roles("READER"))
        ).andExpect(status().isOk());
    }

    @Test
    void deleteAuthor_ShouldAllowAdmin() throws Exception {
        mockMvc.perform(delete("/api/authors/1")
                .with(user("admin").roles("ADMIN"))
                .with(csrf())
        ).andExpect(status().isNoContent());
    }

    @Test
    void deleteAuthor_ShouldForbidReader() throws Exception {
        mockMvc.perform(delete("/api/authors/1")
                .with(user("reader").roles("READER"))
                .with(csrf())
        ).andExpect(status().isForbidden());
    }
}
