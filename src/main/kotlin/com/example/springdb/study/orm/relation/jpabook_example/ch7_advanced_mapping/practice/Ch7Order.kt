package com.example.springdb.study.orm.relation.jpabook_example.ch7_advanced_mapping.practice

import jakarta.persistence.CascadeType
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
class Ch7Order : RegisterUpdateBaseEntity() {
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

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true)
    var orderItems: MutableSet<Ch7OrderItem> = mutableSetOf()

    fun assignMember(member: Ch7Member) {
        this.member = member
        if ( !member.orders.contains(this) ){
            member.orders.add(this)
        }
    }

    fun assignDelivery(delivery: Ch7Delivery) {
        this.delivery = delivery
        if (delivery.order != this) {
            delivery.order = this
        }
    }

    fun assignItem(item: Ch7Item, count: Int) {
        val orderItem = Ch7OrderItem()
        orderItem.order = this
        orderItem.item = item
        orderItem.price = item.price
        orderItem.count = count

        if (!this.orderItems.contains(orderItem)) {
            this.orderItems.add(orderItem)
        }
        if (!item.orderItems.contains(orderItem)) {
            item.orderItems.add(orderItem)
        }
    }
}

