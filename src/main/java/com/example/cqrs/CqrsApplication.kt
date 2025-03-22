package com.example.cqrs

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CqrsApplication

fun main(args: Array<String>) {
    runApplication<CqrsApplication>(*args)
}