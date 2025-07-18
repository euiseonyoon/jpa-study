package com.example.springdb.study.springdata.jpa.specification

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import kotlin.test.assertTrue

@DataJpaTest
class SpecificationTest {

    @Autowired
    lateinit var store2Repository: Store2Repository

    @Autowired
    lateinit var product2Repository: Product2Repository

    @PersistenceContext
    lateinit var em: EntityManager

    private fun addStoreAndProduct(): List<Product2> {
        val savedStore1 = Store2(name = "First Store").let { it ->
            store2Repository.save(it)
        }
        val savedStore2 = Store2(name = "Second Store").let { it ->
            store2Repository.save(it)
        }

        val savedProducts = (0..19).map { it ->
            val (product, store) = if (it <= 9) {
                Product2(name = "$it product", price = it * 100, store = savedStore1) to savedStore1
            } else {
                Product2(name = "$it product", price = it * 120, store = savedStore2) to savedStore2
            }
            store.addProduct(product)
            product2Repository.save(product)
        }
        em.flush()
        return savedProducts
    }

    @Test
    fun test() {
        addStoreAndProduct()

        val allStores = store2Repository.findAll()
        allStores.map { store ->
            assertTrue { store.productList.isNotEmpty() }

            store.productList.map { product ->
                assertTrue { product.store.id == store.id }
                assertTrue { product.store.productList.contains(product) }
            }
        }
        em.clear()

        println("=================================================")
        println("=================================================")

        val stores = store2Repository.findAll(Store2Specs.nameContains("first")) // 이거일때 where절 잘 먹고 product들도 한번에 잘 가져옴
        // val stores = store2Repository.findAll() // 이거일떄 Product 들도 한번에 잘 가져옴

        assertTrue { stores.size == 1 }

        /**
         *
         *    select
         *         s1_0.id,
         *         s1_0.name,
         *         p1_0.store_id,
         *         p1_0.id,
         *         p1_0.name,
         *         p1_0.price
         *     from
         *         store2 s1_0
         *     left join
         *         product2 p1_0
         *             on s1_0.id=p1_0.store_id
         *     where
         *         lower(s1_0.name) like ? escape ''
         *
         * */
    }
}
