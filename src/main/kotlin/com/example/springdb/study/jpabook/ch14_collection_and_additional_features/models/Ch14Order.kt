package com.example.springdb.study.jpabook.ch14_collection_and_additional_features.models

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany


@Entity
class Ch14Order {
    @Id @GeneratedValue
    val id: Long? = null

    @JoinColumn(name = "member_id", nullable = false)
    var member: Ch14Member? = null

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true)
    var orderItems: MutableSet<Ch14OrderItem> = mutableSetOf()

    fun assignMember(member: Ch14Member) {
        this.member = member
        if ( !member.orders.contains(this) ){
            member.orders.add(this)
        }
    }

    fun assignItem(item: Ch14Item, count: Int) {
        val orderItem = Ch14OrderItem()
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
