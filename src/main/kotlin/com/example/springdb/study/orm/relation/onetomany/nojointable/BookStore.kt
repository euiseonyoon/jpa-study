package com.example.springdb.study.orm.relation.onetomany.nojointable

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
class BookStore(
    @Id @GeneratedValue
    val id: Long = 0,
    val name: String = "",
) {
    // 양방향
    @OneToMany(mappedBy = "bookStore")
    val books: MutableSet<Book> = mutableSetOf()

    fun addBook(book: Book) {
        book.bookStore = this
        this.books.add(book)
    }
}