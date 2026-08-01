package com.tbs.repository;

import com.tbs.model.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    List<Invoice> findBySubscriberIdOrderByBillingPeriodEndDesc(Long subscriberId);

    boolean existsBySubscriberIdAndBillingPeriodEnd(Long subscriberId, LocalDate billingPeriodEnd);
}
