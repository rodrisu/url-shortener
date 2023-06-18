package com.example.shortener.controllers;

import com.example.shortener.services.UrlService;
import com.example.shortener.utils.UrlGenerator;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class UrlControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UrlService urlService;

    @Test
    void shortenUrl_ValidUrl_ReturnsCreatedResponse() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://www.google.com\"}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.shortUrl").exists());
    }

    @Test
    void shortenUrl_InvalidUrl_ReturnsBadRequestResponse() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"invalid-url\"}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    void getLongUrl_InvalidShortUrl_ReturnsNotFoundResponse() throws Exception {
        String shortUrlKey = "invalid-key";

        mockMvc.perform(MockMvcRequestBuilders.get("/urls/{shortUrlKey}", shortUrlKey))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    void getLongUrl_ValidShortUrl_ReturnsSuccessResponse() throws Exception {
        String shortUrlKey = getShortUrlKey();
        String longUrl = "https://www.google.com";

        urlService.shortenUrl(longUrl);

        mockMvc.perform(MockMvcRequestBuilders.get("/urls/{shortUrlKey}", shortUrlKey))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.longUrl").value(longUrl));
    }

    @Test
    void redirectUrl_ValidShortUrl_ReturnsPermanentRedirect() throws Exception {
        String shortUrlKey = getShortUrlKey();
        String userAgent = "Mozilla/5.0";

        urlService.shortenUrl("https://www.google.com");

        mockMvc.perform(MockMvcRequestBuilders.get("/{shortUrlKey}", shortUrlKey)
                        .header("User-Agent", userAgent))
                .andExpect(MockMvcResultMatchers.status().isMovedPermanently())
                .andExpect(MockMvcResultMatchers.redirectedUrl("https://www.google.com"));

    }

    @Test
    void redirectUrl_InvalidShortUrl_ReturnsNotFoundResponse() throws Exception {
        String shortUrlKey = "invalid-key";
        String userAgent = "Mozilla/5.0";

        mockMvc.perform(MockMvcRequestBuilders.get("/{shortUrlKey}", shortUrlKey)
                        .header("User-Agent", userAgent))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/not-found"));

    }

    @Test
    void deleteUrl_ValidShortUrl_ReturnsSuccessResponse() throws Exception {
        String shortUrlKey = getShortUrlKey();

        urlService.shortenUrl("https://www.google.com");

        mockMvc.perform(MockMvcRequestBuilders.delete("/urls/{shortUrlKey}", shortUrlKey))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    private String getShortUrlKey() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://www.google.com\"}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.shortUrl").exists())
                .andReturn();
        String responseContent = mvcResult.getResponse().getContentAsString();

        return UrlGenerator.INSTANCE.getKeyFromShortUrl(JsonPath.parse(responseContent).read("$.shortUrl"));
    }
}
