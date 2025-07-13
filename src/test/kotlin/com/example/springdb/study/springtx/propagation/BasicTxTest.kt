package com.example.springdb.study.springtx.propagation

import com.example.springdb.study.logger
import org.junit.jupiter.api.Test
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
import org.springframework.transaction.TransactionStatus
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
}
