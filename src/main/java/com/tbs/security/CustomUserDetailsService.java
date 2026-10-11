package com.tbs.security;

import com.tbs.model.entity.Subscriber;
import com.tbs.repository.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Override
    public UserDetails loadUserByUsername(String msisdn) throws UsernameNotFoundException {
        Subscriber subscriber = subscriberRepository.findByMsisdn(msisdn)
            .orElseThrow(() -> new UsernameNotFoundException("Subscriber not found with MSISDN: " + msisdn));

        return new User(
            subscriber.getMsisdn(),
            subscriber.getPassword(),
            subscriber.isActive(),
            true,
            true,
            true,
            getAuthorities(subscriber)
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Subscriber subscriber) {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_SUBSCRIBER"));
    }
}