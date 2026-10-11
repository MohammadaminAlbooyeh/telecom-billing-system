package com.tbs.batch;

import com.tbs.model.entity.RatedUsage;
import com.tbs.model.entity.Subscriber;
import com.tbs.repository.SubscriberRepository;
import com.tbs.service.RatingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsageAggregationTaskletTest {

    @Mock
    private SubscriberRepository subscriberRepository;

    @Mock
    private RatingService ratingService;

    @Mock
    private StepContribution contribution;

    @Mock
    private ChunkContext chunkContext;

    @InjectMocks
    private UsageAggregationTasklet tasklet;

    @Test
    void ratesPendingUsageForEveryActiveSubscriber() {
        Subscriber active1 = subscriber(1L, true);
        Subscriber active2 = subscriber(2L, true);
        Subscriber inactive = subscriber(3L, false);

        when(subscriberRepository.findAll()).thenReturn(List.of(active1, active2, inactive));
        when(ratingService.ratePendingForSubscriber(1L)).thenReturn(List.of(new RatedUsage()));
        when(ratingService.ratePendingForSubscriber(2L)).thenReturn(List.of(new RatedUsage()));

        RepeatStatus status = tasklet.execute(contribution, chunkContext);

        assertThat(status).isEqualTo(RepeatStatus.FINISHED);
        verify(ratingService).ratePendingForSubscriber(1L);
        verify(ratingService).ratePendingForSubscriber(2L);
        verify(ratingService, never()).ratePendingForSubscriber(3L);
        verify(contribution).incrementWriteCount(2);
    }

    @Test
    void finishesNormallyWhenThereAreNoSubscribers() {
        when(subscriberRepository.findAll()).thenReturn(List.of());

        RepeatStatus status = tasklet.execute(contribution, chunkContext);

        assertThat(status).isEqualTo(RepeatStatus.FINISHED);
        verify(contribution).incrementWriteCount(0);
    }

    private Subscriber subscriber(Long id, boolean active) {
        Subscriber subscriber = new Subscriber();
        subscriber.setId(id);
        subscriber.setActive(active);
        return subscriber;
    }
}
