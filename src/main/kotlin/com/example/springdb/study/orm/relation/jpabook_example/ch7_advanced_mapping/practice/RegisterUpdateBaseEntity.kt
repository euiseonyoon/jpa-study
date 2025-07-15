package com.example.springdb.study.orm.relation.jpabook_example.ch7_advanced_mapping.practice

import jakarta.persistence.MappedSuperclass
import java.time.LocalDateTime

@MappedSuperclass
abstract class RegisterUpdateBaseEntity {
    var createdDate: LocalDateTime = LocalDateTime.now()
    var lastModifiedDate: LocalDateTime? = null
}