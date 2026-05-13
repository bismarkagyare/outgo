package com.outgo.api.domain.shared;

import java.time.Instant;

public interface DomainEvent {
    Instant occuredAt();
}
