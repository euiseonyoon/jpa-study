package com.example.springdb.study.jpabook.ch15_advanced_and_optimizing.visitors

import com.example.springdb.study.jpabook.ch15_advanced_and_optimizing.interfaces.Ch15Visitor
import com.example.springdb.study.jpabook.ch15_advanced_and_optimizing.models.Ch15Album
import com.example.springdb.study.jpabook.ch15_advanced_and_optimizing.models.Ch15Book
import com.example.springdb.study.jpabook.ch15_advanced_and_optimizing.models.Ch15Movie
import com.example.springdb.study.logger

class Ch15TitleVisitor : Ch15Visitor {
    private val log = logger()

    override fun visitAlbum(album: Ch15Album) {
        log.info("album: artist={}, genre={}", album.artist, album.genre)
    }

    override fun visitBook(book: Ch15Book) {
        log.info("book: author={}, isbn={}", book.author, book.isbn)
    }

    override fun visitMovie(movie: Ch15Movie) {
        log.info("movie: director={}, actor={}", movie.director, movie.actor)
    }
}
