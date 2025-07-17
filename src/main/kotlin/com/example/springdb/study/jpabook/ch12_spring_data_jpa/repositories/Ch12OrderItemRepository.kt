package com.example.springdb.study.jpabook.ch12_spring_data_jpa.repositories

import com.example.springdb.study.jpabook.ch12_spring_data_jpa.models.Ch12OrderItem
import com.example.springdb.study.jpabook.ch12_spring_data_jpa.projections.Ch12NestedOrderItemInterface
import org.springframework.data.jpa.repository.JpaRepository

interface Ch12OrderItemRepository: JpaRepository<Ch12OrderItem, Long> {

    fun findByCount(count: Int): List<Ch12NestedOrderItemInterface>
}
