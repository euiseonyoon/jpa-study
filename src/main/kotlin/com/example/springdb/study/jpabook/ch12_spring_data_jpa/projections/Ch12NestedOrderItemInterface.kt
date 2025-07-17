package com.example.springdb.study.jpabook.ch12_spring_data_jpa.projections

import com.example.springdb.study.jpabook.ch12_spring_data_jpa.models.Ch12Member

interface Ch12NestedOrderItemInterface {
    val count: Int
    val order: OrderInfo
    val item: ItemInfo

    interface OrderInfo {
        val member: Ch12Member
    }

    interface ItemInfo {
        val id: Long
        val name: String
        val stockQuantity: Int
    }
}
