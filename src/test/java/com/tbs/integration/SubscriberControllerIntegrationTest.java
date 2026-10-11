package com.tbs.integration;

import com.tbs.model.entity.Subscriber;
import com.tbs.model.entity.Plan;
import com.tbs.model.enums.PlanType;
import com.tbs.repository.SubscriberRepository;
import com.tbs.repository.PlanRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SubscriberControllerIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private PlanRepository planRepository;

    @Test
    void shouldRegisterAndLoginSubscriber() throws Exception {
        Plan plan = new Plan();
        plan.setName("Test Plan");
        plan.setPlanType(PlanType.POSTPAID);
        plan.setMonthlyFee(BigDecimal.valueOf(29.99));
        plan.setCallRatePerMinute(BigDecimal.valueOf(0.10));
        plan.setDataRatePerMb(BigDecimal.valueOf(0.05));
        plan.setSmsRate(BigDecimal.valueOf(0.02));
        plan = planRepository.save(plan);

        String registerJson = """
            {
                "msisdn": "989123456789",
                "firstName": "John",
                "lastName": "Doe",
                "email": "john.doe@example.com",
                "password": "password123",
                "planId": %d
            }
            """.formatted(plan.getId());

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.username").value("989123456789"));

        String loginJson = """
            {
                "msisdn": "989123456789",
                "password": "password123"
            }
            """;

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists())
            .andReturn()
            .getResponse()
            .getContentAsString();

        String token = loginResponse.substring(
            loginResponse.indexOf("\"token\":\"") + 9,
            loginResponse.indexOf("\"", loginResponse.indexOf("\"token\":\"") + 9)
        );

        mockMvc.perform(get("/api/subscribers/msisdn/989123456789")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.msisdn").value("989123456789"));
    }

    @Test
    void shouldIngestCDRAndGenerateUsageRecord() throws Exception {
        Plan plan = new Plan();
        plan.setName("CDR Test Plan");
        plan.setPlanType(PlanType.POSTPAID);
        plan.setMonthlyFee(BigDecimal.valueOf(19.99));
        plan.setCallRatePerMinute(BigDecimal.valueOf(0.15));
        plan.setDataRatePerMb(BigDecimal.valueOf(0.08));
        plan.setSmsRate(BigDecimal.valueOf(0.03));
        plan = planRepository.save(plan);

        String registerJson = """
            {
                "msisdn": "989111222333",
                "firstName": "Jane",
                "lastName": "Smith",
                "email": "jane.smith@example.com",
                "password": "password123",
                "planId": %d
            }
            """.formatted(plan.getId());

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerJson))
            .andExpect(status().isOk());

        String loginJson = """
            {
                "msisdn": "989111222333",
                "password": "password123"
            }
            """;

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        String token = loginResponse.substring(
            loginResponse.indexOf("\"token\":\"") + 9,
            loginResponse.indexOf("\"", loginResponse.indexOf("\"token\":\"") + 9)
        );

        mockMvc.perform(post("/api/usage")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "cdrId": "cdr-test-001",
                        "msisdn": "989111222333",
                        "usageType": "CALL",
                        "timestamp": "2026-03-15T10:30:00",
                        "durationSeconds": 120,
                        "dataVolumeMb": null,
                        "smsCount": null
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.cdrId").value("cdr-test-001"))
            .andExpect(jsonPath("$.msisdn").value("989111222333"));

        Subscriber subscriber = subscriberRepository.findByMsisdn("989111222333").orElseThrow();

        mockMvc.perform(get("/api/usage/subscribers/{id}/records", subscriber.getId())
                .header("Authorization", "Bearer " + token)
                .param("from", "2026-03-01")
                .param("to", "2026-03-31"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].cdrId").value("cdr-test-001"));
    }
}