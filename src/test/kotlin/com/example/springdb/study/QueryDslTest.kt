package com.example.springdb.study

import com.example.springdb.study.springdata.common.querydsl.Account
import com.example.springdb.study.springdata.common.querydsl.AccountRepository
import com.example.springdb.study.springdata.common.querydsl.QAccount
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.util.Optional
import kotlin.test.assertTrue

@DataJpaTest
class QueryDslTest {

    // AccountRepository 가 QuerydslPredicateExecutor implement
    @Autowired
    lateinit var accountRepository: AccountRepository

    @Test
    fun test() {
        val account = QAccount.account
        val predicate = account
            .firstName.containsIgnoreCase("Luke")
            .and(account.lastName.startsWith("Yo"))

        val one: Optional<Account> = accountRepository.findOne(predicate)
        // Optional에서 값이 있는지 확인
        assertTrue { one.isEmpty }

        /*
        Hibernate가 내부에서 생성한 JPQL (Hibernate HQL)
        select
            account
        from
            Account account
        where
            lower(account.firstName) like ?1 escape '!'
            and account.lastName like ?2 escape '!'
         */
        /*
        Hibernate가 JPQL → SQL로 변환해서 DB에 실행한 실제 쿼리
        select
            a1_0.id,
            a1_0.first_name,
            a1_0.last_name
        from
        account a1_0
        where
            lower(a1_0.first_name) like ? escape '!'
            and a1_0.last_name like ? escape '!'
        fetch
            first ? rows only
         */
        // NOTE: 여기서 신기한걸 보았다.  첫
        // 첫번쨰? : "Luke" 대신 "luke"  (이건 뭐 case not sensitive로 hibernate가 치환했나보다)
        // 두번째? : "Yo" 그대로 잘 적용
        // 세번쨰? : 2  근데 이게 이해가 안되었음
        // 세번째? 2 이유:
        // Spring Data JPA의 findOne()이 내부적으로 PageRequest.of(0, 2)을 사용하기 때문
        // findOne의 특성상 0개면 없음   1개면 ok, 2개 이상이면 IncorrectResultSizeDataAccessException를 내려고 한다.
    }
}
