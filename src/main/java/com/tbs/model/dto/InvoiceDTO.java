package com.tbs.model.dto;

import com.tbs.model.entity.Invoice;
import com.tbs.model.entity.InvoiceLineItem;
import com.tbs.model.enums.InvoiceStatus;
import com.tbs.model.enums.UsageType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class InvoiceDTO {

    private Long id;
    private String invoiceNumber;
    private Long subscriberId;
    private String msisdn;
    private LocalDate billingPeriodStart;
    private LocalDate billingPeriodEnd;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private BigDecimal totalAmount;
    private InvoiceStatus status;
    private List<InvoiceLineItemDTO> lineItems;

    public static InvoiceDTO from(Invoice invoice) {
        InvoiceDTO dto = new InvoiceDTO();
        dto.setId(invoice.getId());
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setSubscriberId(invoice.getSubscriber().getId());
        dto.setMsisdn(invoice.getSubscriber().getMsisdn());
        dto.setBillingPeriodStart(invoice.getBillingPeriodStart());
        dto.setBillingPeriodEnd(invoice.getBillingPeriodEnd());
        dto.setIssueDate(invoice.getIssueDate());
        dto.setDueDate(invoice.getDueDate());
        dto.setTotalAmount(invoice.getTotalAmount());
        dto.setStatus(invoice.getStatus());
        dto.setLineItems(invoice.getLineItems().stream().map(InvoiceLineItemDTO::from).toList());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public Long getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(Long subscriberId) {
        this.subscriberId = subscriberId;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public LocalDate getBillingPeriodStart() {
        return billingPeriodStart;
    }

    public void setBillingPeriodStart(LocalDate billingPeriodStart) {
        this.billingPeriodStart = billingPeriodStart;
    }

    public LocalDate getBillingPeriodEnd() {
        return billingPeriodEnd;
    }

    public void setBillingPeriodEnd(LocalDate billingPeriodEnd) {
        this.billingPeriodEnd = billingPeriodEnd;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    public List<InvoiceLineItemDTO> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<InvoiceLineItemDTO> lineItems) {
        this.lineItems = lineItems;
    }

    public static class InvoiceLineItemDTO {

        private Long id;
        private UsageType usageType;
        private String description;
        private BigDecimal amount;

        public static InvoiceLineItemDTO from(InvoiceLineItem lineItem) {
            InvoiceLineItemDTO dto = new InvoiceLineItemDTO();
            dto.setId(lineItem.getId());
            dto.setUsageType(lineItem.getUsageType());
            dto.setDescription(lineItem.getDescription());
            dto.setAmount(lineItem.getAmount());
            return dto;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public UsageType getUsageType() {
            return usageType;
        }

        public void setUsageType(UsageType usageType) {
            this.usageType = usageType;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }
}
