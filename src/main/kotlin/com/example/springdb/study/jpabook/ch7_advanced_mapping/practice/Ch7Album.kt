package com.example.springdb.study.jpabook.ch7_advanced_mapping.practice

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Entity
@DiscriminatorValue("A")
class Ch7Album : Ch7Item() {
    var artist: String? = null

    @Enumerated(EnumType.STRING)
    var genre: Ch7AlbumGenre? = null
}
