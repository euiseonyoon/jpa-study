package com.example.springdb.study.jpabook.ch15_advanced_and_optimizing.models

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Entity
@DiscriminatorValue("A")
class Ch15Album : Ch15Item() {
    var artist: String? = null

    @Enumerated(EnumType.STRING)
    var genre: Ch15AlbumGenre? = null

    override fun getTitle(): String = "album: artist={$artist}, genre={$genre}"

    override fun getTarget(): Any = this
}
