package com.example.springdb.study.springtx.propagation

import com.example.springdb.study.logger
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.UnexpectedRollbackException
import org.springframework.transaction.interceptor.DefaultTransactionAttribute
import javax.sql.DataSource

@SpringBootTest
class BasicTxTest {
    private val log = logger()

    @Autowired
    @Qualifier("testTxManager")
    lateinit var txManager: PlatformTransactionManager

    @TestConfiguration
    class Config {
        @Bean(name = ["testTxManager"]) // MyTransactionManagerConfig.transactionalManager()가 있어서 충돌발생
        fun transactionManager(dataSource: DataSource): PlatformTransactionManager {
            return DataSourceTransactionManager(dataSource)
        }
    }

    @Test
    fun commit() {
        log.info("tx 시작")
        val status: TransactionStatus = txManager.getTransaction(DefaultTransactionAttribute())

        log.info("tx 커밋 시작")
        txManager.commit(status)
        log.info("tx 커밋 완료")

        /**
         * Log:
         *
         * tx 시작
         * Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
         * Acquired Connection [HikariProxyConnection@1798111714 wrapping org.postgresql.jdbc.PgConnection@477bea57] for JDBC transaction
         * tx 커밋 시작
         * Initiating transaction commit
         * Committing JDBC transaction on Connection [HikariProxyConnection@1798111714 wrapping org.postgresql.jdbc.PgConnection@477bea57]
         * Releasing JDBC Connection [HikariProxyConnection@1798111714 wrapping org.postgresql.jdbc.PgConnection@477bea57] after transaction
         * tx 커밋 완료
         *
         * */
    }

    @Test
    fun rollback() {
        log.info("tx 시작")
        val status: TransactionStatus = txManager.getTransaction(DefaultTransactionAttribute())

        log.info("tx 롤백 시작")
        txManager.rollback(status)
        log.info("tx 롤백 완료")

        /**
         * LOG:
         *
         * tx 시작
         * Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
         * Acquired Connection [HikariProxyConnection@1177677045 wrapping org.postgresql.jdbc.PgConnection@5dc7391e] for JDBC transaction
         * tx 롤백 시작
         * Initiating transaction rollback
         * Rolling back JDBC transaction on Connection [HikariProxyConnection@1177677045 wrapping org.postgresql.jdbc.PgConnection@5dc7391e]
         * Releasing JDBC Connection [HikariProxyConnection@1177677045 wrapping org.postgresql.jdbc.PgConnection@5dc7391e] after transaction
         * tx 롤백 완료
         *
         * */
    }

    @Test
    fun doubleCommit() {
        log.info("tx1 시작")
        val tx1: TransactionStatus = txManager.getTransaction(DefaultTransactionAttribute())
        log.info("tx1 커밋 시작")
        txManager.commit(tx1)
        log.info("tx1 커밋 완료")

        log.info("tx2 시작")
        val tx2: TransactionStatus = txManager.getTransaction(DefaultTransactionAttribute())
        log.info("tx2 커밋 시작")
        txManager.commit(tx2)
        log.info("tx2 커밋 완료")

        /**
         * LOG:
         *
         * 아래 로그를 보면 각각의 TX 수행시 다른 db 커넥션을 사용했음을 볼 수 있다.
         *     tx1: HikariProxyConnection@1978823220
         *     tx2: HikariProxyConnection@1680799504
         *
         * tx1 시작
         * Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
         * Acquired Connection [HikariProxyConnection@1978823220 wrapping org.postgresql.jdbc.PgConnection@430212cb] for JDBC transaction
         * tx1 커밋 시작
         * Initiating transaction commit
         * Committing JDBC transaction on Connection [HikariProxyConnection@1978823220 wrapping org.postgresql.jdbc.PgConnection@430212cb]
         * Releasing JDBC Connection [HikariProxyConnection@1978823220 wrapping org.postgresql.jdbc.PgConnection@430212cb] after transaction
         * tx1 커밋 완료
         *
         * tx2 시작
         * Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
         * Acquired Connection [HikariProxyConnection@1680799504 wrapping org.postgresql.jdbc.PgConnection@430212cb] for JDBC transaction
         * tx2 커밋 시작
         * Initiating transaction commit
         * Committing JDBC transaction on Connection [HikariProxyConnection@1680799504 wrapping org.postgresql.jdbc.PgConnection@430212cb]
         * Releasing JDBC Connection [HikariProxyConnection@1680799504 wrapping org.postgresql.jdbc.PgConnection@430212cb] after transaction
         * tx2 커밋 완료
         *
         * */
    }

    @Test
    fun commitAndRollback() {
        log.info("tx1 시작")
        val tx1: TransactionStatus = txManager.getTransaction(DefaultTransactionAttribute())
        log.info("tx1 커밋 시작")
        txManager.commit(tx1)

        log.info("tx2 시작")
        val tx2: TransactionStatus = txManager.getTransaction(DefaultTransactionAttribute())
        log.info("tx2 롤백 시작")
        txManager.rollback(tx2)

        /**
         * LOG:
         *
         * tx1 시작
         * Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
         * Acquired Connection [HikariProxyConnection@1251888103 wrapping org.postgresql.jdbc.PgConnection@67a3dd86] for JDBC transaction
         * tx1 커밋 시작
         * Initiating transaction commit
         * Committing JDBC transaction on Connection [HikariProxyConnection@1251888103 wrapping org.postgresql.jdbc.PgConnection@67a3dd86]
         * Releasing JDBC Connection [HikariProxyConnection@1251888103 wrapping org.postgresql.jdbc.PgConnection@67a3dd86] after transaction
         *
         * tx2 시작
         * Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
         * Acquired Connection [HikariProxyConnection@2109521019 wrapping org.postgresql.jdbc.PgConnection@67a3dd86] for JDBC transaction
         * tx2 롤백 시작
         * Initiating transaction rollback
         * Rolling back JDBC transaction on Connection [HikariProxyConnection@2109521019 wrapping org.postgresql.jdbc.PgConnection@67a3dd86]
         * Releasing JDBC Connection [HikariProxyConnection@2109521019 wrapping org.postgresql.jdbc.PgConnection@67a3dd86] after transaction
         *
         * */
    }

    @Test
    fun inner_commit() {
        log.info("외부 tx 시작")
        val outer = txManager.getTransaction(DefaultTransactionAttribute())
        log.info("outer.isNewTransaction={}", outer.isNewTransaction)

        log.info("내부 tx 시작")
        val inner = txManager.getTransaction(DefaultTransactionAttribute())
        log.info("inner.isNewTransaction={}",inner.isNewTransaction)
        log.info("내부 tx 커밋")
        txManager.commit(inner)

        log.info("외부 tx 커밋")
        txManager.commit(outer)

        /**
         * JpaTransactionManager은 AbstractPlatformTransactionManager를 상속
         * AbstractPlatformTransactionManager는 내부적으로 TransactionSynchronizationManager을 사용한다
         * TransactionSynchronizationManager는 트랜잭션의 리소스와 동기화 정보를 현재 스레드의 ThreadLocal에 저장하여 관리한다.
         * 따라서 트랜잭션의 동기화 정보는 결국 ThreadLocal을 통해 스레드별로 관리된다.
         *
         * LOG:
         *      아래의 로그를 보면 외부,내부 논리적 트렌젝션들이 하나의 물리 트렌젝션으로 묶임을 알 수 있다.
         *      그래서 최후에 물리 트렌젝션 commit이 한번만 이루어졌다.
         *
         *      HikariProxyConnection@754468537
         *
         *      1. outer가 transaction 생성 : outer.isNewTransaction=true
         *      2. inner에서 그 transaction을 그대로 사용:
         *          Participating in existing transaction
         *          inner.isNewTransaction=false
         *
         *
         * 외부 tx 시작
         * Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
         * Acquired Connection [HikariProxyConnection@754468537 wrapping org.postgresql.jdbc.PgConnection@68229a6] for JDBC transaction
         * outer.isNewTransaction=true
         *
         * 내부 tx 시작
         * Participating in existing transaction
         * inner.isNewTransaction=false
         * 내부 tx 커밋
         *
         * 외부 tx 커밋
         * Initiating transaction commit
         * Committing JDBC transaction on Connection [HikariProxyConnection@754468537 wrapping org.postgresql.jdbc.PgConnection@68229a6]
         * Releasing JDBC Connection [HikariProxyConnection@754468537 wrapping org.postgresql.jdbc.PgConnection@68229a6] after transaction
         *
         * */
    }

    @Test
    fun outer_rollback() {
        log.info("외부 tx 시작")
        val outer = txManager.getTransaction(DefaultTransactionAttribute())

        log.info("내부 tx 시작")
        val inner = txManager.getTransaction(DefaultTransactionAttribute())
        log.info("내부 tx 커밋")
        txManager.commit(inner)

        log.info("외부 tx 롤백")
        txManager.rollback(outer)

        /**
         * 결과: inner, outer 둘 다 롤백됨
         *      Initiating transaction rollback.
         *
         * LOG:
         *
         * 외부 tx 시작
         * Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
         * Acquired Connection [HikariProxyConnection@1967943387 wrapping org.postgresql.jdbc.PgConnection@2674ca88] for JDBC transaction
         *
         * 내부 tx 시작
         * Participating in existing transaction
         * 내부 tx 커밋
         *
         * 외부 tx 롤백
         * Initiating transaction rollback.
         * Rolling back JDBC transaction on Connection [HikariProxyConnection@1967943387 wrapping org.postgresql.jdbc.PgConnection@2674ca88]
         * Releasing JDBC Connection [HikariProxyConnection@1967943387 wrapping org.postgresql.jdbc.PgConnection@2674ca88] after transaction
         *
         * */
    }

    @Test
    fun inner_rollback() {
        log.info("외부 tx 시작")
        val outer = txManager.getTransaction(DefaultTransactionAttribute())

        log.info("내부 tx 시작")
        val inner = txManager.getTransaction(DefaultTransactionAttribute())
        log.info("내부 tx 롤백")
        txManager.rollback(inner)

        log.info("외부 tx 커밋")
        assertThrows<UnexpectedRollbackException> {
            txManager.commit(outer)
        }

        /**
         * 설명:
         *      1. outer가 물리 트렌젝션을 시작한다.
         *      2. 내부 트렌젝션은 기존 트렌젝션이 참여한다.
         *      3. 내부 트렌젝션에서 롤백하면 실제 물리 트렌젝션은 롤백하지 않는다.
         *      4. 대신, 내부 트렌젝션에서는 기존 트렌젝션을 롤백 전용이라고 마킹한다.
         *      5. 그래서 외부 트렌젝션에서 기존 물리 트렌젝션을 커밋하려고 한다
         *      6. 이때 "이건 롤백 전용" 마크로 인해 물리 트렌젝션이 롤백된다. (외부, 내부 트렌젝션 모두 롤백된다.)
         *      7. UnexpectedRollbackException를 발생시켜 에러를 전파한다.
         *
         * 에러 발생:
         *      org.springframework.transaction.UnexpectedRollbackException: Transaction rolled back because it has been marked as rollback-only
         *
         * 아래 로그에서:
         *      내부 tx 롤백
         *      Participating transaction failed - marking existing transaction as rollback-only
         *
         * LOG:
         *
         * 외부 tx 시작
         * Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
         * Acquired Connection [HikariProxyConnection@861440872 wrapping org.postgresql.jdbc.PgConnection@a97a895] for JDBC transaction
         *
         * 내부 tx 시작
         * Participating in existing transaction
         * 내부 tx 롤백
         * Participating transaction failed - marking existing transaction as rollback-only.
         * Setting JDBC transaction [HikariProxyConnection@861440872 wrapping org.postgresql.jdbc.PgConnection@a97a895] rollback-only
         *
         * 외부 tx 커밋
         * Global transaction is marked as rollback-only but transactional code requested commit
         * Initiating transaction rollback
         * Rolling back JDBC transaction on Connection [HikariProxyConnection@861440872 wrapping org.postgresql.jdbc.PgConnection@a97a895]
         * Releasing JDBC Connection [HikariProxyConnection@861440872 wrapping org.postgresql.jdbc.PgConnection@a97a895] after transaction
         *
         * */
    }

    @Test
    fun inner_rollback_requires_new() {
        log.info("외부 tx 시작")
        val outer = txManager.getTransaction(DefaultTransactionAttribute())
        log.info("outer.isNewTransaction={}", outer.isNewTransaction)

        log.info("내부 tx 시작")
        val innerDefinition = DefaultTransactionAttribute()
        // 기존 트렌젝션이 있으면, 기존 트렌젝션은 SUSPEND하고 새로운 트렌젝션을 만든다. (트렌젝션 분리)
        innerDefinition.propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRES_NEW
        val inner = txManager.getTransaction(innerDefinition)
        log.info("inner.isNewTransaction={}", inner.isNewTransaction)

        log.info("내부 tx 롤백")
        txManager.rollback(inner)

        log.info("외부 tx 커밋")
        txManager.commit(outer)

        /**
         *
         * 아래 로그에서:
         *      외부 트렌젝션은: HikariProxyConnection@1047220049
         *      내부 트렌젝션은: HikariProxyConnection@1047220049  -> 서로 다른 물리 트렌젝션을 가짐을 확인 할 수 있다.
         *
         *      내부 트렌젝션 시작 후
         *          Suspending current transaction, creating new transaction with name [null]  <- 기존 트렌젝션 중지
         *          Acquired Connection  <- 새로운 물리 트렌젝션(inner) 획득
         *      내부 트렌젝션 롤백 후
         *          Releasing JDBC Connection [HikariProxyConnection@1047220049  <-- 위에서 얻은 inner 트렌젝션 릴리즈
         *          Resuming suspended transaction after completion of inner transaction  <-- outer 트렌젝션으로 복귀
         *
         * 주의점:
         *      이처럼 REQUIRES_NEW를 사용하게 되면 여러개의 물리 트렌젝션이 사용되고, 이로인해 여러 물리 커넥션이 사용되게 된다.
         *      커넥션풀의 개수가 제한적인 경우, 여러 물리 커넥션을 사용하게 되면, 물리 커넥션 풀에서 커넥션의 개수가 부족한 사태가 발생 할 수 있다.
         *
         * LOG:
         *
         * 외부 tx 시작
         * Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
         * Acquired Connection [HikariProxyConnection@1452044766 wrapping org.postgresql.jdbc.PgConnection@20e48e63] for JDBC transaction
         * outer.isNewTransaction=true  <--- 새 물리 트렌젝션 획득
         *
         * 내부 tx 시작
         * Suspending current transaction, creating new transaction with name [null]
         * Acquired Connection [HikariProxyConnection@1047220049 wrapping org.postgresql.jdbc.PgConnection@3f51fcf5] for JDBC transaction
         * inner.isNewTransaction=true  <--- 새 물리 트렌젝션 획득
         * 내부 tx 롤백
         * Initiating transaction rollback  <--- inner 물리 트렌젝션 롤백
         * Rolling back JDBC transaction on Connection [HikariProxyConnection@1047220049 wrapping org.postgresql.jdbc.PgConnection@3f51fcf5]
         * Releasing JDBC Connection [HikariProxyConnection@1047220049 wrapping org.postgresql.jdbc.PgConnection@3f51fcf5] after transaction
         *
         * Resuming suspended transaction after completion of inner transaction
         * 외부 tx 커밋
         * Initiating transaction commit    <---- outer 물리 트렌젝션 커밋
         * Committing JDBC transaction on Connection [HikariProxyConnection@1452044766 wrapping org.postgresql.jdbc.PgConnection@20e48e63]
         * Releasing JDBC Connection [HikariProxyConnection@1452044766 wrapping org.postgresql.jdbc.PgConnection@20e48e63] after transaction
         *
         * */
    }
}
