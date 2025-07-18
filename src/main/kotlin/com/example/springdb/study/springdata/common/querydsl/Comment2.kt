package com.example.springdb.study.springdata.common.querydsl

import jakarta.annotation.Generated
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class Comment2 {

    @Id @Generated
    val id: Long = 0

    var content: String = ""

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    var post: Post2? = null
}
