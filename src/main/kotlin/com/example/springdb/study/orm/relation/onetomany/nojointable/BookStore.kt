package com.example.springdb.study.orm.relation.onetomany.nojointable

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
class BookStore {
    @Id @GeneratedValue
    val id: Long? = null
    var name: String? = null

    // 양방향
    @OneToMany(mappedBy = "bookStore")
    val books: MutableSet<Book> = mutableSetOf()

    fun addBook(book: Book) {
        this.books.add(book)
        // 만약 Book의 bookStore setter 처럼 되어있다면, 무한 루프가 발생할수 있다.
        // 이를 방지하기 위한 코드
        if(book.bookStore != this) {
            book.bookStore = this
        }
    }
}