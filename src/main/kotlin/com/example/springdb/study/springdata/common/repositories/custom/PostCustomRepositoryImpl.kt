package com.example.springdb.study.springdata.common.repositories.custom

import com.example.springdb.study.springdata.common.repositories.models.Post
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class PostCustomRepositoryImpl(
    private val entityManager: EntityManager
) : PostCustomRepository{
    override fun findAllPost(): List<Post> {
        return entityManager.createQuery("SELECT p FROM Post p", Post::class.java).resultList
    }

    override fun delete(entity: Post) {
        println("CUSTOM DELETE")
        entityManager.remove(entity)
    }
}
