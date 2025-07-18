package com.example.springdb.study.springdata.jpa.entityGraph

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.NamedAttributeNode
import jakarta.persistence.NamedEntityGraph
import jakarta.persistence.OneToMany

@Entity
@NamedEntityGraph(
    name = "Store.withProducts",
    attributeNodes = [
        NamedAttributeNode("products")
    ]
)
class Store(
    @Id @GeneratedValue
    val id: Long? = null,
    val name: String
) {
    @OneToMany(mappedBy = "store")
    private val products: MutableSet<Product> = mutableSetOf()

    val productList: List<Product>
        get() = products.toList()

    fun addProduct(product: Product) {
        this.products.add(product)
    }
}
