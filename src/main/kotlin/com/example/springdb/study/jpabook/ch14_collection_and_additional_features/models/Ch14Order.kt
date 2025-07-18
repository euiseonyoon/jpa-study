package com.example.springdb.study.jpabook.ch14_collection_and_additional_features.models

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.NamedAttributeNode
import jakarta.persistence.NamedEntityGraph
import jakarta.persistence.NamedEntityGraphs
import jakarta.persistence.NamedSubgraph
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne

@NamedEntityGraphs(
    value = [
        NamedEntityGraph(
            name = "Ch14Order.withMember",
            attributeNodes = [
                NamedAttributeNode("member")
            ]
        ),

        NamedEntityGraph(
            name = "Ch14Order.withAll",
            attributeNodes = [
                NamedAttributeNode("member"),
                NamedAttributeNode("orderItems", subgraph = "orderItemsSubGraph")
            ],
            subgraphs = [
                NamedSubgraph(
                    name = "orderItemsSubGraph",
                    attributeNodes = [
                        // Ch14OrderItem.item의 필드명과 일치하게
                        NamedAttributeNode("item")
                    ]
                )
            ]
        )
    ]
)
@Entity
class Ch14Order {
    @Id @GeneratedValue
    val id: Long? = null

    // 엔티티 그래프의 동작을 명확히 보기 위해, 의도적으로 FetchType.LAZY를 설정했다.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    var member: Ch14Member? = null

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true)
    var orderItems: MutableSet<Ch14OrderItem> = mutableSetOf()

    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    var delivery: Ch14Delivery? = null

    fun assignDelivery(delivery: Ch14Delivery) {
        this.delivery = delivery
        if (delivery.order != this) {
            delivery.order = this
        }
    }

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
