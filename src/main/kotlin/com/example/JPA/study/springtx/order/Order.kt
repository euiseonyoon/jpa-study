package com.example.JPA.study.springtx.order

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "orders")
class Order(
    var processCase: String, // 정상, 예외, 잔고부족
) {
    @Id
    @GeneratedValue
    val id: Long? = null
    var payStatus: String? = null // 대기, 완료
}