package com.example.springdb.study.jpabook.ch12_spring_data_jpa.repositories

import com.example.springdb.study.jpabook.ch12_spring_data_jpa.models.Ch12OrderItem
import com.example.springdb.study.jpabook.ch12_spring_data_jpa.projections.Ch12NestedOrderItemDto
import com.example.springdb.study.jpabook.ch12_spring_data_jpa.projections.Ch12NestedOrderItemInterface
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface Ch12OrderItemRepository : JpaRepository<Ch12OrderItem, Long> {

    fun findByCount(count: Int): List<Ch12NestedOrderItemInterface>

    @Query("SELECT oi FROM Ch12OrderItem oi WHERE oi.count = :count")
    fun searchByCountToNestedInterface(@Param("count") count: Int): List<Ch12NestedOrderItemInterface>

    @Query(
        """
        SELECT
            new com.example.springdb.study.jpabook.ch12_spring_data_jpa.projections.Ch12NestedOrderItemDto(
                oi.count,
                new com.example.springdb.study.jpabook.ch12_spring_data_jpa.projections.Ch12OrderDto(oi.order.id),
                new com.example.springdb.study.jpabook.ch12_spring_data_jpa.projections.Ch12ItemDto(oi.item.id, oi.item.name)
            )
        FROM Ch12OrderItem oi
        WHERE oi.count = :count
    """
    )
    fun searchByCountToNestedDto(@Param("count") count: Int): List<Ch12NestedOrderItemDto>
}
