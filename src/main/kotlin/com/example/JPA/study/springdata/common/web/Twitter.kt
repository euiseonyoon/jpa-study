package com.example.JPA.study.springdata.common.web

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import java.time.Clock
import java.time.LocalDateTime

@Entity
class Twitter(
    @Id @GeneratedValue
    val id: Long = 0,
    var title: String,
    val created: LocalDateTime = LocalDateTime.now(Clock.systemUTC())
) {
}