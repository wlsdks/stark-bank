package com.example.cqrs.common.service;

import java.time.LocalDateTime;

public interface EventReplayService {

    void replayEvents(String accountId, LocalDateTime fromDate);

}
