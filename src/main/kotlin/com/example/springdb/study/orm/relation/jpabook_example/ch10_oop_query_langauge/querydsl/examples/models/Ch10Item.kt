package com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
class Ch10Item {
    @Id @GeneratedValue
    val id: Long? = null

    @Column(nullable = false)
    var name: String? = null

    var price: Int = 0
    var stockQuantity: Int = 0

    constructor()
    constructor(name: String, price: Int, stockQuantity: Int) {
        this.name = name
        this.price = price
        this.stockQuantity = stockQuantity
    }
}