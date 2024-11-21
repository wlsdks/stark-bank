package com.example.cqrs.entity.read.write.event.base;

import java.time.LocalDateTime;

/**
 * 모든 이벤트는 AccountEvent 인터페이스를 구현하고, BaseAccountEvent 추상 클래스를 상속받습니다.
 */
public interface AccountEvent {

    String getAccountId();
    LocalDateTime getEventDate();

}
