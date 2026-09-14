# Test Design

## Equivalence partitioning
Telemetry partitions: CRITICAL_LOW, LOW, NORMAL, HIGH, CRITICAL_HIGH. Mission duration partitions: below 10 minutes, valid 10 minutes–30 days, above 30 days. Configuration interval partitions: below 5 seconds, valid 5–300 seconds, above 300 seconds.

## Boundary value analysis
Test just below, at, and just above the telemetry thresholds. For configuration interval use 4.99/5/5.01 and 299.99/300/300.01. For mission duration use 9/10/11 minutes and 30 days minus one minute/30 days/30 days plus one minute.

## Decision table
Primary decision: alert severity. Conditions are breached voltage, weak signal, critical temperature, and high packet loss. Zero breaches = no alert; one breach = warning; two or more = critical.

## State transition testing
Satellite: REGISTERED → COMMISSIONING → OPERATIONAL → DEGRADED/SAFE_MODE → OPERATIONAL; DECOMMISSIONED is terminal. Mission: DRAFT → SCHEDULED → ACTIVE → COMPLETED, with cancellation allowed from DRAFT/SCHEDULED/ACTIVE. Alert: NEW → ESCALATED → ACKNOWLEDGED → RESOLVED, with condition-clear auto-resolution from an open state.
