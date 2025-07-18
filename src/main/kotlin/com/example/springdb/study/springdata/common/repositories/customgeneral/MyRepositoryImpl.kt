package com.example.springdb.study.springdata.common.repositories.customgeneral

import jakarta.persistence.EntityManager
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import java.io.Serializable

class MyRepositoryImpl<T, ID : Serializable>(
    private val entityInformation: JpaEntityInformation<T, *>,
    private val entityManager: EntityManager
) : SimpleJpaRepository<T, ID>(entityInformation, entityManager),
    MyRepository<T, ID> {
    override fun contains(entity: T): Boolean {
        return entityManager.contains(entity)
    }
}
