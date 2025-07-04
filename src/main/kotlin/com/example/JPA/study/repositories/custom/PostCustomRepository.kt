package com.example.JPA.study.repositories.custom

import com.example.JPA.study.repositories.models.Post

interface PostCustomRepository {
    fun findAllPost(): List<Post>
    fun delete(entity: Post )
}