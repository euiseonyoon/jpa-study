package com.example.JPA.study.springdata.jpa.projection

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import kotlin.test.assertTrue

@DataJpaTest
class ProjectionTest {

    @Autowired
    lateinit var posts: PostWithLikeCommentsRepository

    @Autowired
    lateinit var comments: CommentWithLikesRepository

    @PersistenceContext
    lateinit var em: EntityManager

    @Test
    fun test() {

        val post = PostWithLikeComments(title = "title", content = "this is content")
        val savedPost = posts.save(post)
        val comment = CommentWithLikes(content = "this is good post")
        comment.likes = 10
        comment.dislikes = 3
        savedPost.addCommentWithLikes(comment)
        val savedComment = comments.save(comment)

        em.flush()
        em.clear()

        // val commentList = comments.findWithPost(savedPost.id!!)
        val commentList = comments.findCommentDtoById(savedPost.id!!)

        assertTrue { commentList.size == 1 }
        assertTrue { commentList.first().likes == 10 }
        assertTrue { commentList.first().dislikes == 3 }
        assertTrue { commentList.first().score == 7 }
    }
}