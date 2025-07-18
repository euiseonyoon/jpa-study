package com.example.springdb.study.springdata.mystudy

import com.example.springdb.study.logger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MyTransactionManagerService() {
    private val log = logger()

    @Transactional(transactionManager = "myTransactionManager")
    fun customTxManager() {
        log.info("[MyTransactionalManagerService] customTxManager()")
    }
}
