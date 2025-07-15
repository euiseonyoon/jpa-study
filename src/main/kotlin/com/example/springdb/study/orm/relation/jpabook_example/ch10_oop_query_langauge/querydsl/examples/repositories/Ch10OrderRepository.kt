package com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.repositories

import com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.models.Ch10Order
import org.springframework.data.jpa.repository.JpaRepository

interface Ch10OrderRepository: JpaRepository<Ch10Order, Long> {
}