package com.example.springdb.study.jpabook.ch15_advanced_and_optimizing.interfaces

import com.example.springdb.study.jpabook.ch15_advanced_and_optimizing.models.Ch15Album
import com.example.springdb.study.jpabook.ch15_advanced_and_optimizing.models.Ch15Book
import com.example.springdb.study.jpabook.ch15_advanced_and_optimizing.models.Ch15Movie

interface Ch15Visitor {
    fun visitAlbum(album: Ch15Album)

    fun visitBook(book: Ch15Book)

    fun visitMovie(movie: Ch15Movie)
}
