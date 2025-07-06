package com.example.JPA.study.springdata.jpa.specification

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne

@Entity
class Product2(
    @Id @GeneratedValue
    val id: Long? = null,
    val name: String,
    val price: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    // Store2에 EntityGraph사용 말고,
    // 여기서 @Batchsize 넣는것도 1+N 을 최소화 하는데 도움이 된다.
    val store: Store2
) {
}
