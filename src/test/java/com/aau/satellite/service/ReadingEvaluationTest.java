package com.aau.satellite.service;

import static org.junit.jupiter.api.Assertions.*;

import com.aau.satellite.domain.Partition;
import org.junit.jupiter.api.Test;

class ReadingEvaluationTest {

    @Test
    void isClear_true_when_nothing_breached() {
        ReadingEvaluation e =
                new ReadingEvaluation(
                        Partition.NORMAL, Partition.NORMAL, Partition.NORMAL, false, false, false, false);
        assertTrue(e.isClear());
        assertEquals(0, e.breachCount());
    }

    @Test
    void isClear_false_when_voltage_low() {
        ReadingEvaluation e =
                new ReadingEvaluation(
                        Partition.LOW, Partition.NORMAL, Partition.NORMAL, true, false, false, false);
        assertFalse(e.isClear());
        assertEquals(1, e.breachCount());
    }

    @Test
    void isClear_false_when_signal_weak() {
        ReadingEvaluation e =
                new ReadingEvaluation(
                        Partition.NORMAL, Partition.NORMAL, Partition.LOW, false, true, false, false);
        assertFalse(e.isClear());
        assertEquals(1, e.breachCount());
    }

    @Test
    void isClear_false_when_temp_critical() {
        ReadingEvaluation e =
                new ReadingEvaluation(
                        Partition.NORMAL, Partition.CRITICAL_HIGH, Partition.NORMAL, false, false, true, false);
        assertFalse(e.isClear());
        assertEquals(1, e.breachCount());
    }

    @Test
    void isClear_false_when_packet_loss_high() {
        ReadingEvaluation e =
                new ReadingEvaluation(
                        Partition.NORMAL, Partition.NORMAL, Partition.NORMAL, false, false, false, true);
        assertFalse(e.isClear());
        assertEquals(1, e.breachCount());
    }

    @Test
    void breachCount_counts_all_four_simultaneous_breaches() {
        ReadingEvaluation e =
                new ReadingEvaluation(
                        Partition.CRITICAL_LOW,
                        Partition.CRITICAL_HIGH,
                        Partition.CRITICAL_LOW,
                        true,
                        true,
                        true,
                        true);
        assertEquals(4, e.breachCount());
        assertFalse(e.isClear());
    }
}