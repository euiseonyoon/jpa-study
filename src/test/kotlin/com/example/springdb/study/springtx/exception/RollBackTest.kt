package com.example.springdb.study.springtx.exception

import com.example.springdb.study.logger
import com.example.springdb.study.springtx.exception.code.MyException
import com.example.springdb.study.springtx.exception.code.RollbackService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class RollBackTest {
    private val log = logger()

    @Autowired
    lateinit var service: RollbackService

    @Test
    fun runtimeException() {
        assertThrows<RuntimeException>{ service.runtimeException() }
        /**
         * LOG:
         *
         * PROPAGATION_REQUIRED,ISOLATION_DEFAULT
         * Opened new EntityManager [SessionImpl(37257991<open>)] for JPA transaction
         * Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@7eb22e87]
         * Getting transaction for [RollbackService.runtimeException]
         * call runtimeException
         * java.lang.RuntimeException
         * Initiating transaction rollback. <------- 롤백됨
         * Rolling back JPA transaction on EntityManager [SessionImpl(37257991<open>)]
         * Closing JPA EntityManager [SessionImpl(37257991<open>)] after transaction
         *
         * */
    }

    @Test
    fun checkedException() {
        assertThrows<MyException>{ service.checkedException() }
        /**
         * LOG:
         *
         * PROPAGATION_REQUIRED,ISOLATION_DEFAULT
         * Opened new EntityManager [SessionImpl(1129590231<open>)] for JPA transaction
         * Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@48b97191]
         * Getting transaction for [RollbackService.checkedException]
         * call checkedException
         * com.example.springdb.study.MyException
         * Initiating transaction commit         <----- 커밋됨
         * Committing JPA transaction on EntityManager [SessionImpl(1129590231<open>)]
         * Closing JPA EntityManager [SessionImpl(1129590231<open>)] after transaction
         *
         * */
    }

    @Test
    fun rollbackFor() {
        assertThrows<MyException>{ service.rollbackFor() }

        /**
         * LOG:
         *
         * PROPAGATION_REQUIRED,ISOLATION_DEFAULT,-com.example.springdb.study.MyException
         * Opened new EntityManager [SessionImpl(1712600096<open>)] for JPA transaction
         * Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@5bcafda3]
         * Getting transaction for [RollbackService.rollbackFor]
         * call rollbackFor
         * com.example.springdb.study.MyException
         * Initiating transaction rollback    <------------- 체크드 예외지만  롤백!!
         * Rolling back JPA transaction on EntityManager [SessionImpl(1712600096<open>)]
         * Closing JPA EntityManager [SessionImpl(1712600096<open>)] after transaction
         *
         * */
    }
}
