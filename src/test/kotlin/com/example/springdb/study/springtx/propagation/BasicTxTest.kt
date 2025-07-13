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
}
