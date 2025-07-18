package com.example.springdb.study.jpabook.ch6_relational_mapping

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
class Ch6V1Order {
    @Id @GeneratedValue
    val id: Long? = null

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    var member: Ch6V1Member? = null

    @OneToOne
    @JoinColumn(name = "delivery_id")
    var delivery: Ch6V1Delivery? = null

    @Column(nullable = false)
    var orderDate: LocalDateTime = LocalDateTime.now()

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: Ch6V1OrderStatus = Ch6V1OrderStatus.READY

    @OneToMany(mappedBy = "order")
    var orderItems: MutableSet<Ch6V1OrderItem> = mutableSetOf()

    fun assignDelivery(delivery: Ch6V1Delivery) {
        this.delivery = delivery
        if (delivery.order != this) {
            delivery.order = this
        }
    }
}
