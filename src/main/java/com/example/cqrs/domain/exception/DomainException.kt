package com.example.cqrs.domain.exception

/**
 * 도메인 예외 클래스들
 */
class InsufficientBalanceException(message: String) : RuntimeException(message)
class InvalidOperationException(message: String) : RuntimeException(message)