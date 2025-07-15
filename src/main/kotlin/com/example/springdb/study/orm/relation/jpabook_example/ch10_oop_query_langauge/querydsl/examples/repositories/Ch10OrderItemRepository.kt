package com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.repositories

import com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.models.Ch10OrderItem
import org.springframework.data.jpa.repository.JpaRepository

interface Ch10OrderItemRepository: JpaRepository<Ch10OrderItem, Long> {
}