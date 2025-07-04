package com.example.JPA.study.repositories.domainevent

import com.example.JPA.study.repositories.models.Post
import org.springframework.context.ApplicationEvent

class PostPublishedEvent(
    private val post: Post,
): ApplicationEvent(post) {

    fun getPost() : Post = this.post
}