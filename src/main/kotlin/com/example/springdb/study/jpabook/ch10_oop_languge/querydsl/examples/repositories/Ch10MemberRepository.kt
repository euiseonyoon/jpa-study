package com.example.springdb.study.jpabook.ch10_oop_languge.querydsl.examples.repositories

import com.example.springdb.study.jpabook.ch10_oop_languge.querydsl.examples.models.Ch10Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface Ch10MemberRepository : JpaRepository<Ch10Member, Long>, QuerydslPredicateExecutor<Ch10Member>
