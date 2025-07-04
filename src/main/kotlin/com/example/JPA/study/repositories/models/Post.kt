package com.example.JPA.study.repositories.models

import jakarta.annotation.Generated
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import java.util.Date


@Entity
class Post {

    @Id @Generated
    val id: Long = 0

    var title: String = ""

    var content: String = ""

    @Temporal(TemporalType.TIMESTAMP)
    var created: Date = Date()

    // 양방향, 주인은 Comment( Comment가 Post로 향하는 foreign key를 가지고 있어서)
    @OneToMany(mappedBy = "post", cascade = [CascadeType.ALL], orphanRemoval = true)
    open var comments: MutableSet<Comment> = mutableSetOf()

    fun addComment(comment: Comment) {
        comment.post = this
        this.comments.add(comment)
    }
}

