package com.example.springdb.study.springdata.common.repositories.domainevent

import org.springframework.context.event.EventListener

// NOTE: 둘다 사용 가능
//class PostEventListener : ApplicationListener<PostPublishedEvent>{
//    override fun onApplicationEvent(event: PostPublishedEvent) {
//        println("==============================")
//        println(event.getPost().toString() + "is published!")
//        println("==============================")
//    }
//}

class PostEventListener {

    @EventListener
    fun onPostEvent(event: PostPublishedEvent) {
        println("==============================")
        println(event.getPost().toString() + "is published!")
        println("==============================")
    }
}