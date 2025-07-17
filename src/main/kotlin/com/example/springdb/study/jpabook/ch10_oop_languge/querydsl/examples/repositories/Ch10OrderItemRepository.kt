package com.example.springdb.study.jpabook.ch10_oop_languge.querydsl.examples.repositories

import com.example.springdb.study.jpabook.ch10_oop_languge.querydsl.examples.models.Ch10OrderItem
import org.springframework.data.jpa.repository.JpaRepository

interface Ch10OrderItemRepository: JpaRepository<Ch10OrderItem, Long> {
}