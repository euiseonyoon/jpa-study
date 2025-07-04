package com.example.JPA.study.springdata.common.querydsl

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

// reactive webflux 일때는 ReactiveQuerydslPredicateExecutor를 사용하면 되겠다.
interface AccountRepository: JpaRepository<Account, Long>, QuerydslPredicateExecutor<Account> {
}