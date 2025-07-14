package com.example.springdb.study.orm.relation.manytomany

import jakarta.persistence.EntityManager
import org.hibernate.Session
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class ManyToManyTest {

    @Autowired
    lateinit var em: EntityManager

    @Test
    fun test() {
        val session = em.unwrap(Session::class.java)

        val worker = Worker(name = "new Worker")
        session.persist(worker)
        val roles = listOf(
            Role(name = "ADMIN"),
            Role(name = "USER"),
        ).also { it ->
            it.forEach {session.persist(it)}
        }
        session.flush()

        val workerRoles = WorkerRole.addWorker(worker, roles)
        workerRoles.forEach {session.persist(it)}
        session.flush()
    }

}