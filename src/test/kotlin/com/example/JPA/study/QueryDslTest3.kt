package com.example.JPA.study

import com.example.JPA.study.springdata.common.querydsl.Account
import com.example.JPA.study.springdata.common.querydsl.AccountRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import kotlin.test.assertTrue

@DataJpaTest
class QueryDslTest3 {

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Test
    fun test() {
        val account = Account(
            firstName = "luke",
            lastName = "Yoo"
        )
        accountRepository.save(account)

        val result = accountRepository.findByFirstName("luke")

        assertTrue { result.content.isNotEmpty() }
    }
}
