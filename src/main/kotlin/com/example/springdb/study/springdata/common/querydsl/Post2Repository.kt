package com.example.springdb.study.springdata.common.querydsl

import com.example.springdb.study.springdata.common.repositories.customgeneral.MyRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

// QueryDsl
interface Post2Repository : MyRepository<Post2, Long>, QuerydslPredicateExecutor<Post2>

/*
NOTE: 예전에는 위처럼 사용하면 안되었다. 이유는
    MyRepository의 구현체 : MyRepositoryImpl (최종적으로 SimpleJpaRepository)
    QuerydslPredicateExecutor의 구현체: QuerydslJpaRepository  -> 이건 SimpleJpaRepository를 상속받은 하위 클래스
    그래서 MyRepositoryImpl에서 QuerydslJpaRepository의 내용을 구현해 놓지 않았기 때문에 에러 발생

    하지만 QuerydslPredicateExecutor의 구현체가 QuerydslJpaPredicateExecutor로 바뀜 (이건 SimpleJpaRepository를 상속받지 않는다)
*/
