package com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.repositories

import com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.models.Ch10Item
import com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.models.Ch10Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface Ch10ItemRepository : JpaRepository<Ch10Item, Long>, QuerydslPredicateExecutor<Ch10Item>{
}