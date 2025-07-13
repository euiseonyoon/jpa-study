package com.example.springdb.study.springdata.common.querydsl

import com.querydsl.core.QueryResults
import com.querydsl.core.types.dsl.PathBuilder
import com.querydsl.jpa.JPQLQuery
import jakarta.persistence.EntityManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.Querydsl
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport

// 이런식으로 fragment화 시켜서 lower 하게도 사용가능 하다.
class AccountExtensionImpl(
    private val entityManager: EntityManager,
) : QuerydslRepositorySupport(Account::class.java), AccountExtension {

    // 그냥 val queryDsl 이라고 쓰면 QuerydslRepositorySupport 구현체의 getQuerydsl()과 함수 시그니쳐 충돌일어남
    val querydslHelp : Querydsl by lazy {
        val builder = PathBuilder(Account::class.java, "account")
        Querydsl(entityManager, builder)
    }
    val pageable: Pageable = PageRequest.of(0, 10)

    override fun findByFirstName(partOfFirstName: String): Page<Account> {
        val account = QAccount.account

        val query: JPQLQuery<Account> = from(account)
            .where(account.firstName.equalsIgnoreCase(partOfFirstName))
            // .leftJoin<>()
            // . 어쩌고 저쩌고

        val pageableQuery: JPQLQuery<Account> = querydslHelp.applyPagination(pageable, query)
        val results: QueryResults<Account> = pageableQuery.fetchResults()
        return PageImpl(results.results, pageable, results.total)
    }
}