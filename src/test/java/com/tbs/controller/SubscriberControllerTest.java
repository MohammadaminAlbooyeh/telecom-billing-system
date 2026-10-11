package com.tbs.controller;

import com.tbs.model.entity.Subscriber;
import com.tbs.security.JwtAuthenticationFilter;
import com.tbs.security.CustomUserDetailsService;
import com.tbs.security.SecurityConfig;
import com.tbs.service.BalanceService;
import com.tbs.service.SubscriberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SubscriberController.class)
@Import({SecurityConfig.class})
class SubscriberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubscriberService subscriberService;

    @MockBean
    private BalanceService balanceService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(username = "989120000000", roles = "SUBSCRIBER")
    void registerReturnsCreatedSubscriber() throws Exception {
        Subscriber subscriber = new Subscriber();
        subscriber.setId(1L);
        subscriber.setMsisdn("989120000000");
        subscriber.setFirstName("Ali");
        subscriber.setLastName("Test");
        subscriber.setEmail("ali@example.com");

        when(subscriberService.register(eq("989120000000"), eq("Ali"), eq("Test"), eq("ali@example.com"), eq(2L), anyString()))
                .thenReturn(subscriber);

        mockMvc.perform(post("/api/subscribers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"msisdn\":\"989120000000\",\"firstName\":\"Ali\","
                                + "\"lastName\":\"Test\",\"email\":\"ali@example.com\",\"planId\":2,\"password\":\"password123\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "989120000000", roles = "SUBSCRIBER")
    void getByMsisdnReturnsSubscriber() throws Exception {
        Subscriber subscriber = new Subscriber();
        subscriber.setId(1L);
        subscriber.setMsisdn("989120000000");
        subscriber.setFirstName("Test");
        subscriber.setLastName("User");

        when(subscriberService.getByMsisdn("989120000000")).thenReturn(subscriber);

        mockMvc.perform(get("/api/subscribers/msisdn/989120000000"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "989120000000", roles = "SUBSCRIBER")
    void setActiveUsesQueryParam() throws Exception {
        Subscriber subscriber = new Subscriber();
        subscriber.setId(1L);
        subscriber.setActive(false);

        when(subscriberService.setActive(1L, false)).thenReturn(subscriber);

        mockMvc.perform(patch("/api/subscribers/1/active").param("active", "false"))
                .andExpect(status().isOk());
    }
}