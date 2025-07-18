package com.example.springdb.study.jpabook.ch7_advanced_mapping.practice

import jakarta.persistence.MappedSuperclass
import java.time.LocalDateTime

@MappedSuperclass
abstract class RegisterUpdateBaseEntity {
    var createdDate: LocalDateTime = LocalDateTime.now()
    var lastModifiedDate: LocalDateTime? = null
}
