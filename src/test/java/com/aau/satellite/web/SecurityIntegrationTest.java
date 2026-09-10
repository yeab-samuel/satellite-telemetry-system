package com.aau.satellite.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

  @Autowired MockMvc mvc;

  @Test
  void login_page_is_public_and_css_is_public() throws Exception {
    mvc.perform(get("/login")).andExpect(status().isOk()).andExpect(view().name("login"));

    mvc.perform(get("/app.css"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith("text/css"));
  }

  @Test
  void seeded_operator_can_login() throws Exception {
    mvc.perform(
            post("/login")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("username", "operator")
                .param("password", "operator123"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/dashboard"));
  }

  @Test
  void wrong_password_returns_to_login_with_error() throws Exception {
    mvc.perform(
            post("/login")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("username", "operator")
                .param("password", "wrong-password"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/login?error"));
  }
}
