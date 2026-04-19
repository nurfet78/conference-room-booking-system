package org.nurfet.bookingsystem.validation;

import java.time.Instant;

public interface TimeRangeValidatable {
    Instant startTime();
    Instant endTime();
}
