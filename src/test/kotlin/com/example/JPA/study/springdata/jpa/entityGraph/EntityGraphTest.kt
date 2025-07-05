package com.example.JPA.study.springdata.jpa.entityGraph

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class EntityGraphTest {

    @PersistenceContext
    lateinit var em: EntityManager

    @Autowired
    lateinit var productRepository: ProductRepository
    @Autowired
    lateinit var storeRepository: StoreRepository

    private fun addStoreAndProduct(): List<Product> {
        val savedStore1 = Store(name = "First Store").let { it ->
            storeRepository.save(it)
        }
        val savedStore2 = Store(name = "Second Store").let { it ->
            storeRepository.save(it)
        }

        val savedProducts = (0..19).map { it ->
            val product = if (it <= 9) {
                Product(name = "$it product", price = it * 100, store = savedStore1)
            } else {
                Product(name = "$it product", price = it * 120, store = savedStore2)
            }
            productRepository.save(product)
        }
        em.flush()
        return savedProducts
    }

    @Test
    fun test() {
        addStoreAndProduct()
        em.clear()

        // 1 + N
        val foundStores = storeRepository.findAll()
        println("================================================")
        println("================================================")
        foundStores.forEach { store ->
            store.productList.forEach {
                println("--- ${store.id} : ${it.name}")
            }
        }
    }

    @Test
    fun test2() {
        addStoreAndProduct()
        em.clear()

        val foundStores = storeRepository.findAllWithProducts()
        println("================================================")
        println("================================================")
        foundStores.forEach { store ->
            store.productList.forEach {
                println("--- ${store.id} : ${it.name}")
            }
        }
    }
}