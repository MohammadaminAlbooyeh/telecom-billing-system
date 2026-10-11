package com.tbs.batch;

import com.tbs.model.entity.Subscriber;
import com.tbs.repository.SubscriberRepository;
import com.tbs.service.RatingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Rates all pending (unrated) usage for every active subscriber. This step runs
 * before invoice generation so that the invoices reflect fully rated usage.
 */
@Component
public class UsageAggregationTasklet implements Tasklet {

    private static final Logger log = LoggerFactory.getLogger(UsageAggregationTasklet.class);

    private final SubscriberRepository subscriberRepository;
    private final RatingService ratingService;

    public UsageAggregationTasklet(SubscriberRepository subscriberRepository, RatingService ratingService) {
        this.subscriberRepository = subscriberRepository;
        this.ratingService = ratingService;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        List<Subscriber> subscribers = subscriberRepository.findAll();
        int ratedCount = 0;
        for (Subscriber subscriber : subscribers) {
            if (!subscriber.isActive()) {
                continue;
            }
            ratedCount += ratingService.ratePendingForSubscriber(subscriber.getId()).size();
        }
        contribution.incrementWriteCount(ratedCount);
        log.info("Rated {} usage records across {} subscribers", ratedCount, subscribers.size());
        return RepeatStatus.FINISHED;
    }
}

