package com.example.springdb.study.jpabook.ch6_relational_mapping

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["order", "item"])
    ]
)
class Ch6V1OrderItem {
    @Id @GeneratedValue
    val id: Long? = null

    @Column(nullable = false)
    var price: Int? = null

    @Column(nullable = false)
    var count: Int? = null

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    var order: Ch6V1Order? = null

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    var item: Ch6V1Item? = null
}
