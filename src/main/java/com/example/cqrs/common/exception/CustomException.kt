package com.example.cqrs.common.exception

class ConcurrencyException(message: String) : RuntimeException(message)
class EventHandlingException(message: String) : RuntimeException(message)
class EventReplayException(message: String) : RuntimeException(message)
class InsufficientBalanceException(message: String) : RuntimeException(message)