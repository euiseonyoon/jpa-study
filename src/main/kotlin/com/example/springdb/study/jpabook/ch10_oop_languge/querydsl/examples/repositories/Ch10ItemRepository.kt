package com.example.springdb.study.jpabook.ch10_oop_languge.querydsl.examples.repositories

import com.example.springdb.study.jpabook.ch10_oop_languge.querydsl.examples.models.Ch10Item
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface Ch10ItemRepository : JpaRepository<Ch10Item, Long>, QuerydslPredicateExecutor<Ch10Item>{
}