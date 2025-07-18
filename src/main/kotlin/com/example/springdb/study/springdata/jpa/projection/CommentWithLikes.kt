package com.example.springdb.study.springdata.jpa.projection

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne

@Entity
class CommentWithLikes(
    @Id @GeneratedValue
    val id: Long? = null,
    val content: String
) {
    var likes: Int = 0
    var dislikes: Int = 0

    @ManyToOne()
    var post: PostWithLikeComments? = null
}
