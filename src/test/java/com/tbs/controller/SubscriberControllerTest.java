package com.tbs.controller;

import com.tbs.model.entity.Subscriber;
import com.tbs.service.BalanceService;
import com.tbs.service.SubscriberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SubscriberController.class)
class SubscriberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubscriberService subscriberService;

    @MockBean
    private BalanceService balanceService;

    @Test
    void registerReturnsCreatedSubscriber() throws Exception {
        Subscriber subscriber = new Subscriber();
        subscriber.setId(1L);
        subscriber.setMsisdn("989120000000");
        subscriber.setName("Ali");
        subscriber.setEmail("ali@example.com");

        when(subscriberService.register(eq("989120000000"), eq("Ali"), eq("ali@example.com"), eq(2L)))
                .thenReturn(subscriber);

        mockMvc.perform(post("/api/subscribers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"msisdn\":\"989120000000\",\"name\":\"Ali\","
                                + "\"email\":\"ali@example.com\",\"planId\":2}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.msisdn").value("989120000000"));
    }

    @Test
    void getByMsisdnReturnsSubscriber() throws Exception {
        Subscriber subscriber = new Subscriber();
        subscriber.setId(1L);
        subscriber.setMsisdn("989120000000");

        when(subscriberService.getByMsisdn("989120000000")).thenReturn(subscriber);

        mockMvc.perform(get("/api/subscribers/msisdn/989120000000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msisdn").value("989120000000"));
    }

    @Test
    void setActiveUsesQueryParam() throws Exception {
        Subscriber subscriber = new Subscriber();
        subscriber.setId(1L);
        subscriber.setActive(false);

        when(subscriberService.setActive(1L, false)).thenReturn(subscriber);

        mockMvc.perform(patch("/api/subscribers/1/active").param("active", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }
}
