package com.example.springdb.study.springdata.common.querydsl

import jakarta.annotation.Generated
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import java.time.Clock
import java.time.LocalDateTime

@Entity
class Post2{

    @Id @Generated
    val id: Long = 0

    var title: String = ""

    var content: String = ""

    var created: LocalDateTime = LocalDateTime.now(Clock.systemUTC())

    // 양방향, 주인은 Comment( Comment가 Post로 향하는 foreign key를 가지고 있어서)
    @OneToMany(mappedBy = "post", cascade = [CascadeType.ALL], orphanRemoval = true)
    open var comments: MutableSet<Comment2> = mutableSetOf()

    fun addComment(comment: Comment2) {
        comment.post = this
        this.comments.add(comment)
    }
}

