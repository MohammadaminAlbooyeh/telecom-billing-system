package com.tbs.cdr;

import com.tbs.exception.DuplicateCdrException;
import com.tbs.repository.UsageRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CdrDeduplicationServiceTest {

    @Mock
    private UsageRecordRepository usageRecordRepository;

    @InjectMocks
    private CdrDeduplicationService deduplicationService;

    @Test
    void isDuplicateReturnsTrueWhenExists() {
        when(usageRecordRepository.existsByCdrId("cdr-1")).thenReturn(true);

        assertThat(deduplicationService.isDuplicate("cdr-1")).isTrue();
    }

    @Test
    void isDuplicateReturnsFalseWhenNotExists() {
        when(usageRecordRepository.existsByCdrId("cdr-1")).thenReturn(false);

        assertThat(deduplicationService.isDuplicate("cdr-1")).isFalse();
    }

    @Test
    void isDuplicateReturnsFalseForNullIdWithoutQuerying() {
        assertThat(deduplicationService.isDuplicate(null)).isFalse();

        verifyNoInteractions(usageRecordRepository);
    }

    @Test
    void checkDuplicateThrowsWhenDuplicate() {
        when(usageRecordRepository.existsByCdrId("cdr-1")).thenReturn(true);

        assertThatThrownBy(() -> deduplicationService.checkDuplicate("cdr-1"))
                .isInstanceOf(DuplicateCdrException.class)
                .hasMessageContaining("cdr-1");
    }

    @Test
    void checkDuplicateDoesNotThrowWhenUnique() {
        when(usageRecordRepository.existsByCdrId("cdr-1")).thenReturn(false);

        deduplicationService.checkDuplicate("cdr-1");
    }
}

