package com.example.JPA.study.springdata.common.querydsl

import org.springframework.data.domain.Page
import org.springframework.transaction.annotation.Transactional


@Transactional(readOnly = true)
interface AccountExtension {
    fun findByFirstName(partOfFirstName: String): Page<Account>
}