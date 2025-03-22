package com.example.cqrs.common.exception

class ConcurrencyException(message: String) : RuntimeException(message)
class EventHandlingException(message: String) : RuntimeException(message)
class EventReplayException(message: String) : RuntimeException(message)

/**
 * 도메인 예외 클래스들
 */
class InsufficientBalanceException(message: String) : RuntimeException(message)
class InvalidOperationException(message: String) : RuntimeException(message)