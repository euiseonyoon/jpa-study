package com.example.springdb.study.orm.relation.jpabook_example.ch7_advanced_mapping.practice

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import java.time.LocalDateTime

@Entity
class Ch7Order {
    @Id @GeneratedValue
    val id: Long? = null

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    var member: Ch7Member? = null

    @OneToOne
    @JoinColumn(name = "delivery_id")
    var delivery: Ch7Delivery? = null

    @Column(nullable = false)
    var orderDate: LocalDateTime = LocalDateTime.now()

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: Ch7OrderStatus = Ch7OrderStatus.READY

    @OneToMany(mappedBy = "order")
    var orderItems: MutableSet<Ch7OrderItem> = mutableSetOf()

    fun assignDelivery(delivery: Ch7Delivery) {
        this.delivery = delivery
        if (delivery.order != this) {
            delivery.order = this
        }
    }
}

