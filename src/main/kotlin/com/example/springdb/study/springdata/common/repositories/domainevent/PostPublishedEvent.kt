package com.example.springdb.study.springdata.common.repositories.domainevent

import com.example.springdb.study.springdata.common.repositories.models.Post
import org.springframework.context.ApplicationEvent

class PostPublishedEvent(
    private val post: Post
) : ApplicationEvent(post) {

    fun getPost(): Post = this.post
}
