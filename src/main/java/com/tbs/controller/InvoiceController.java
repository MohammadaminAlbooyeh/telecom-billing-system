package com.tbs.controller;

import com.tbs.model.dto.InvoiceDTO;
import com.tbs.service.InvoiceGenerationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceGenerationService invoiceGenerationService;

    public InvoiceController(InvoiceGenerationService invoiceGenerationService) {
        this.invoiceGenerationService = invoiceGenerationService;
    }

    @PostMapping("/generate")
    public ResponseEntity<InvoiceDTO> generate(@RequestParam Long subscriberId,
                                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodStart,
                                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodEnd) {
        InvoiceDTO invoice = InvoiceDTO.from(
                invoiceGenerationService.generateInvoice(subscriberId, periodStart, periodEnd));
        return ResponseEntity.status(HttpStatus.CREATED).body(invoice);
    }

    @PostMapping("/generate-all")
    public List<InvoiceDTO> generateAll(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodStart,
                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodEnd) {
        return invoiceGenerationService.generateInvoicesForAll(periodStart, periodEnd).stream()
                .map(InvoiceDTO::from)
                .toList();
    }

    @GetMapping("/subscribers/{subscriberId}")
    public List<InvoiceDTO> getInvoicesForSubscriber(@PathVariable Long subscriberId) {
        return invoiceGenerationService.getInvoicesForSubscriber(subscriberId).stream()
                .map(InvoiceDTO::from)
                .toList();
    }

    @GetMapping("/{invoiceNumber}")
    public InvoiceDTO getByInvoiceNumber(@PathVariable String invoiceNumber) {
        return InvoiceDTO.from(invoiceGenerationService.getByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Invoice not found with number: " + invoiceNumber)));
    }
}
