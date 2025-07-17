package com.example.springdb.study.jpabook.ch12_spring_data_jpa.models

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany

@Entity
class Ch12Order {
    @Id @GeneratedValue
    val id: Long? = null

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    var member: Ch12Member? = null

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true)
    var orderItems: MutableSet<Ch12OrderItem> = mutableSetOf()

    fun assignMember(member: Ch12Member) {
        this.member = member
        if ( !member.orders.contains(this) ){
            member.orders.add(this)
        }
    }

    fun assignItem(item: Ch12Item, count: Int) {
        val orderItem = Ch12OrderItem()
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