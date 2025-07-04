package com.example.JPA.study

import com.example.JPA.study.repositories.custom.PostRepository
import com.example.JPA.study.repositories.domainevent.PostListenerConfig
import com.example.JPA.study.repositories.domainevent.PostPublishedEvent
import com.example.JPA.study.repositories.models.Post
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Import
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@DataJpaTest
@Import(PostListenerConfig::class)
class PostCustomRepoTest3 {

    @Autowired
    lateinit var postRepository: PostRepository

    // implementing ApplicationEventPublisher
    @Autowired
    lateinit var applicationContext: ApplicationContext

    @Test
    fun test1() {
        // applicationContext = AnnotationConfigApplicationContext 객체
        val post = Post()
        post.title = "title"
        post.content = "content"

        val event = PostPublishedEvent(post)
        applicationContext.publishEvent(event)
    }
}
