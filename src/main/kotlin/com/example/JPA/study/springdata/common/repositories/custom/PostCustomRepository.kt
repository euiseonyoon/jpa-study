package com.example.JPA.study.springdata.common.repositories.custom

import com.example.JPA.study.springdata.common.repositories.models.Post

interface PostCustomRepository {
    fun findAllPost(): List<Post>
    fun delete(entity: Post )
}