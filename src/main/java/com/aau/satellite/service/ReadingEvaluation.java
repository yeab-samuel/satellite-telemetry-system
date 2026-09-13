package com.aau.satellite.service;

import com.aau.satellite.domain.Partition;

/**
 * Represents the result of evaluating a satellite telemetry reading.
 *
 * <p>The evaluation contains the partitions for each telemetry value together with flags indicating
 * whether any safety thresholds were breached.
 */
public record ReadingEvaluation(
    Partition voltagePartition,
    Partition temperaturePartition,
    Partition signalPartition,
    boolean voltageLow,
    boolean signalWeak,
    boolean tempCritical,
    boolean packetLossHigh) {

  /**
   * Determines whether the reading is completely healthy.
   *
   * @return true when no telemetry threshold has been breached
   */
  public boolean isClear() {
    return !voltageLow && !signalWeak && !tempCritical && !packetLossHigh;
  }

  /**
   * Counts the number of threshold breaches.
   *
   * @return number of breached telemetry conditions
   */
  public int breachCount() {
    int count = 0;

    if (voltageLow) {
      count++;
    }

    if (signalWeak) {
      count++;
    }

    if (tempCritical) {
      count++;
    }

    if (packetLossHigh) {
      count++;
    }

    return count;
  }
}
