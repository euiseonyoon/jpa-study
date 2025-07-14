package com.example.springdb.study.springtx.propagation

import com.example.springdb.study.logger
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

@Repository
class LogRepository(
    @PersistenceContext
    private val em: EntityManager
) {
    private val log = logger()

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun save(logMessage: Log) {
        log.info("log 저장")
        em.persist(logMessage)

        if (logMessage.message.contains("로그예외")) {
            log.info("log 저장시 예외 발생")
            throw RuntimeException("예외 발생!")
        }
    }

    fun find(message: String): Optional<Log> {
        return em.createQuery("SELECT l FROM Log l WHERE l.message = :message", Log::class.java)
            .setParameter("message", message)
            .resultList.stream().findAny();
    }
}
