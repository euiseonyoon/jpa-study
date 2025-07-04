package com.example.JPA.study.repositories.custom

import com.example.JPA.study.repositories.models.Post
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository

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
