package com.example.springdb.study.jpabook.ch7_advanced_mapping.examples.mappedsuperclass

import jakarta.persistence.Entity

@Entity
class Ch7V1Phone : Ch7V1BaseEntity() {
    // id, name을 상속
    var isSmartPhone: Boolean = true
}
