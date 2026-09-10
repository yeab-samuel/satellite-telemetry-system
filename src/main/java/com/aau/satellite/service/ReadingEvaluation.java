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
    boolean packetLossHigh,
    boolean packetLossCritical) {

  /**
   * Determines whether the reading is completely healthy.
   *
   * @return true when no telemetry threshold has been breached
   */
  public boolean isClear() {
    return !voltageLow && !signalWeak && !tempCritical && !packetLossHigh;
  }

  /**
   * Counts the number of threshold breaches that feed the decision table.
   *
   * <p>packetLossCritical is deliberately NOT counted here: it isn't a fifth
   * independent condition, it's a refinement of packetLossHigh (packet loss
   * above the critical threshold is always also above the "high" one). It's
   * used separately, as a severity override, in AlertEvaluationService.
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
