package com.example.JPA.study.springdata.common.repositories.models

import jakarta.annotation.Generated
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class Comment {

    @Id @Generated
    val id: Long = 0

    var content: String = ""

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    var post: Post? = null
}