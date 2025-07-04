package com.example.JPA.study

import com.example.JPA.study.repositories.custom.PostRepository
import com.example.JPA.study.repositories.models.Post
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@DataJpaTest
class PostCustomRepoTest2 {

    @Autowired
    lateinit var postRepository: PostRepository

    @Test
    fun test() {
        postRepository.findAllPost()
    }

    @Test
    fun test2() {
        val post = Post()
        post.title = "post title"
        post.content = "post content"

        assertFalse { postRepository.contains(post) }
    }

    @Test
    fun test3() {
        val post = Post()
        post.title = "post title"
        post.content = "post content"

        postRepository.save(post)
        assertTrue { postRepository.contains(post) }
    }

    @Test
    fun test4() {
        val post = Post()
        post.title = "post title"
        post.content = "post content"

        postRepository.save(post)
        postRepository.delete(post)
        assertTrue { postRepository.contains(post) }
    }
}
