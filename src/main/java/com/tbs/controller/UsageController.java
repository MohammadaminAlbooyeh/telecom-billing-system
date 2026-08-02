package com.tbs.controller;

import com.tbs.cdr.CdrIngestionService;
import com.tbs.model.dto.CdrDTO;
import com.tbs.model.dto.UsageSummaryDTO;
import com.tbs.model.entity.RatedUsage;
import com.tbs.model.entity.UsageRecord;
import com.tbs.service.RatingService;
import com.tbs.service.UsageMeteringService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/usage")
public class UsageController {

    private final CdrIngestionService cdrIngestionService;
    private final UsageMeteringService usageMeteringService;
    private final RatingService ratingService;

    public UsageController(CdrIngestionService cdrIngestionService,
                           UsageMeteringService usageMeteringService,
                           RatingService ratingService) {
        this.cdrIngestionService = cdrIngestionService;
        this.usageMeteringService = usageMeteringService;
        this.ratingService = ratingService;
    }

    @PostMapping("/cdr")
    public ResponseEntity<UsageRecord> ingestRaw(@RequestBody String rawCdr) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cdrIngestionService.ingest(rawCdr));
    }

    @PostMapping
    public ResponseEntity<UsageRecord> ingest(@RequestBody CdrDTO cdr) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cdrIngestionService.ingest(cdr));
    }

    @PostMapping("/batch")
    public ResponseEntity<List<UsageRecord>> ingestAll(@RequestBody List<CdrDTO> cdrs) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cdrIngestionService.ingestAll(cdrs));
    }

    @PostMapping("/batch/raw")
    public ResponseEntity<List<UsageRecord>> ingestRawBatch(@RequestBody List<String> rawCdrs) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cdrIngestionService.ingestBatch(rawCdrs));
    }

    @GetMapping("/subscribers/{subscriberId}/records")
    public List<UsageRecord> getUsageRecords(@PathVariable Long subscriberId,
                                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return usageMeteringService.getUsageRecords(subscriberId, from, to);
    }

    @GetMapping("/subscribers/{subscriberId}/summary")
    public UsageSummaryDTO getUsageSummary(@PathVariable Long subscriberId,
                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return usageMeteringService.getUsageSummary(subscriberId, from, to);
    }

    @GetMapping("/subscribers/{subscriberId}/unrated-count")
    public long countUnrated(@PathVariable Long subscriberId) {
        return usageMeteringService.countUnrated(subscriberId);
    }

    @PostMapping("/{usageRecordId}/rate")
    public RatedUsage rate(@PathVariable Long usageRecordId) {
        return ratingService.rateUsage(usageRecordId);
    }

    @PostMapping("/subscribers/{subscriberId}/rate")
    public List<RatedUsage> ratePendingForSubscriber(@PathVariable Long subscriberId) {
        return ratingService.ratePendingForSubscriber(subscriberId);
    }

    @PostMapping("/rate-all")
    public List<RatedUsage> rateAllPending() {
        return ratingService.rateAllPending();
    }
}
