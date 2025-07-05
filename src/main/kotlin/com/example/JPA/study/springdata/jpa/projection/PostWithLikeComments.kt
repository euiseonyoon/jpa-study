package com.example.JPA.study.springdata.jpa.projection

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
class PostWithLikeComments(
    @Id @GeneratedValue
    val id: Long? = null,

    val title: String,
    val content: String,
) {
    @OneToMany(mappedBy = "post")
    private val comments: MutableSet<CommentWithLikes> = mutableSetOf()
    val commentList: List<CommentWithLikes>
        get() = comments.map { it }

    fun addCommentWithLikes(comment: CommentWithLikes) {
        comment.post = this
        this.comments.add(comment)
    }
}