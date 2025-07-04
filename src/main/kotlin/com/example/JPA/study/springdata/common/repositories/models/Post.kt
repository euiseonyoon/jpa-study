package com.example.JPA.study.springdata.common.repositories.models

import com.example.JPA.study.springdata.common.repositories.domainevent.PostPublishedEvent
import jakarta.annotation.Generated
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import org.springframework.data.domain.AbstractAggregateRoot
import java.time.Clock
import java.time.LocalDateTime

// NOTE: 이런식으로
@Entity
class Post : AbstractAggregateRoot<Post>() {

    @Id @Generated
    val id: Long = 0

    var title: String = ""

    var content: String = ""

    var created: LocalDateTime = LocalDateTime.now(Clock.systemUTC())

    // 양방향, 주인은 Comment( Comment가 Post로 향하는 foreign key를 가지고 있어서)
    @OneToMany(mappedBy = "post", cascade = [CascadeType.ALL], orphanRemoval = true)
    open var comments: MutableSet<Comment> = mutableSetOf()

    fun addComment(comment: Comment) {
        comment.post = this
        this.comments.add(comment)
    }

    fun publish(): Post {
        val event = PostPublishedEvent(this)
        this.registerEvent(event)
        return this
    }
}

