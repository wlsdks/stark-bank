package com.example.cqrs.controller;

import com.example.cqrs.service.EventReplayService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@RestController
public class AccountAdminController {

    private final EventReplayService eventReplayService;

    /**
     * 특정 계좌의 읽기 모델을 재구성합니다.
     */
    @PostMapping("/{accountId}/replay")
    public ResponseEntity<String> replayEvents(
            @PathVariable String accountId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate
    ) {
        fromDate = fromDate != null ? fromDate : LocalDateTime.of(1970, 1, 1, 0, 0);
        eventReplayService.replayEvents(accountId, fromDate);
        return ResponseEntity.ok("이벤트 재생이 완료되었습니다.");
    }

}