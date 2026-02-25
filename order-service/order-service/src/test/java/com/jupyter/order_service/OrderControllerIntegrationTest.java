package com.jupyter.order_service;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig().dynamicPort())
            .build();

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void setup(DynamicPropertyRegistry registry) {
        registry.add("eureka.client.enabled", () -> false);
        registry.add("spring.cloud.openfeign.client.config.user-service.url",
                () -> wireMockServer.getRuntimeInfo().getHttpBaseUrl());
    }

    @Test
    void shouldFailWith401WhenTokenIsMissing() throws Exception {
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": 1,
                                  "description": "Pedido sem token"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Token de autenticação ausente para chamada Feign."));

        wireMockServer.verify(0, getRequestedFor(urlEqualTo("/users/1")));
    }

    @Test
    void shouldForwardBearerTokenAndReturnSuccess() throws Exception {
        wireMockServer.stubFor(get(urlEqualTo("/users/1"))
                .withHeader("Authorization", equalTo("Bearer valid-token"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "id": 1,
                                  "name": "Alice",
                                  "email": "alice@email.com"
                                }
                                """)));

        mockMvc.perform(post("/orders")
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": 1,
                                  "description": "Pedido com token"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("Pedido criado para o usuário: Alice"));

        wireMockServer.verify(getRequestedFor(urlEqualTo("/users/1"))
                .withHeader("Authorization", equalTo("Bearer valid-token")));
    }
}
