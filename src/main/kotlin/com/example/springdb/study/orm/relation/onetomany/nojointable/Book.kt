package com.example.springdb.study.orm.relation.onetomany.nojointable

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne

@Entity
class Book(
    @Id @GeneratedValue
    val id: Long = 0,
    val title: String = "",
) {
    @ManyToOne(fetch = FetchType.LAZY)
    var bookStore: BookStore? = null
        set(value) {
            field = value
            value?.addBook(this)  // ②
        }
}