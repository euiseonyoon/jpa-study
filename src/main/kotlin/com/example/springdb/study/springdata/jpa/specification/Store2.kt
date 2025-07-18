package com.example.springdb.study.springdata.jpa.specification

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.NamedAttributeNode
import jakarta.persistence.NamedEntityGraph
import jakarta.persistence.OneToMany

@Entity
@NamedEntityGraph(
    name = "Store2.withProducts",
    attributeNodes = [
        NamedAttributeNode("products")
    ]
)
class Store2(
    @Id @GeneratedValue
    val id: Long? = null,
    val name: String
) {
    @OneToMany(mappedBy = "store")
    private val products: MutableSet<Product2> = mutableSetOf()

    val productList: List<Product2>
        get() = products.toList()

    fun addProduct(product: Product2) {
        this.products.add(product)
    }
}
