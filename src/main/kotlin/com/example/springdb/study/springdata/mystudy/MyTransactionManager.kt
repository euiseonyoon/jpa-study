package com.example.springdb.study.springdata.mystudy

import com.example.springdb.study.logger
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.SimpleTransactionStatus

class MyTransactionManager: PlatformTransactionManager {
    private val log = logger()

    override fun getTransaction(definition: TransactionDefinition?): TransactionStatus {
        log.info("[MyTransactionManager] getTransaction")
        return  SimpleTransactionStatus()
    }

    override fun commit(status: TransactionStatus) {
        log.info("[MyTransactionManager] commit")
    }

    override fun rollback(status: TransactionStatus) {
        log.info("[MyTransactionManager] rollback")
    }
}