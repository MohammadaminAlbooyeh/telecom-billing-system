package com.tbs.repository;

import com.tbs.model.entity.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {

    Optional<Subscriber> findByMsisdn(String msisdn);

    boolean existsByMsisdn(String msisdn);
}
