package com.example.springdb.study.springtx.propagation

import com.example.springdb.study.logger
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

@Repository
class AudienceRepository(
    @PersistenceContext
    private val em: EntityManager
) {
    private val log = logger()

    @Transactional
    fun save(audience: Audience) {
        log.info("member 저장")
        em.persist(audience)
    }

    fun find(username: String): Optional<Audience> {
        return em.createQuery("SELECT a FROM Audience a WHERE a.username = :username", Audience::class.java)
            .setParameter("username", username)
            .resultList.stream().findAny();
    }
}