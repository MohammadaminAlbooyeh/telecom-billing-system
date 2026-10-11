package com.tbs.security;

import com.tbs.model.dto.AuthRequest;
import com.tbs.model.dto.AuthResponse;
import com.tbs.model.dto.RegisterRequest;
import com.tbs.model.entity.Plan;
import com.tbs.model.entity.Subscriber;
import com.tbs.model.enums.PlanType;
import com.tbs.repository.PlanRepository;
import com.tbs.repository.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getMsisdn(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(token, "Bearer", userDetails.getUsername()));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (subscriberRepository.existsByMsisdn(request.getMsisdn())) {
            return ResponseEntity.badRequest().build();
        }

        Plan plan = planRepository.findById(request.getPlanId())
            .orElseGet(() -> {
                Plan defaultPlan = new Plan();
                defaultPlan.setName("Default Postpaid");
                defaultPlan.setPlanType(PlanType.POSTPAID);
                defaultPlan.setMonthlyFee(BigDecimal.valueOf(29.99));
                defaultPlan.setCallRatePerMinute(BigDecimal.valueOf(0.10));
                defaultPlan.setDataRatePerMb(BigDecimal.valueOf(0.05));
                defaultPlan.setSmsRate(BigDecimal.valueOf(0.02));
                return planRepository.save(defaultPlan);
            });

        Subscriber subscriber = new Subscriber();
        subscriber.setMsisdn(request.getMsisdn());
        subscriber.setFirstName(request.getFirstName());
        subscriber.setLastName(request.getLastName());
        subscriber.setEmail(request.getEmail());
        subscriber.setPassword(passwordEncoder.encode(request.getPassword()));
        subscriber.setPlan(plan);
        subscriber.setActive(true);
        subscriberRepository.save(subscriber);

        String token = jwtUtil.generateToken(request.getMsisdn(), "ROLE_SUBSCRIBER");
        return ResponseEntity.ok(new AuthResponse(token, "Bearer", request.getMsisdn()));
    }
}