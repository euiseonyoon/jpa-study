package com.example.JPA.study.repositories.domainevent

import org.springframework.context.ApplicationListener

class PostEventListener : ApplicationListener<PostPublishedEvent>{
    override fun onApplicationEvent(event: PostPublishedEvent) {
        println("==============================")
        println(event.getPost().toString() + "is published!")
        println("==============================")
    }
}