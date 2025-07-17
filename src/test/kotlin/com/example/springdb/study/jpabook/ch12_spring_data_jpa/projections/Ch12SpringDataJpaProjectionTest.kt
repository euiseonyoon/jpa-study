package com.example.springdb.study.jpabook.ch12_spring_data_jpa.projections

import com.example.springdb.study.jpabook.ch12_spring_data_jpa.models.Ch12Item
import com.example.springdb.study.jpabook.ch12_spring_data_jpa.repositories.Ch12ItemRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import kotlin.ranges.random
import kotlin.test.assertEquals

@DataJpaTest
class Ch12SpringDataJpaProjectionTest {

    @Autowired
    @PersistenceContext
    lateinit var em: EntityManager

    @Autowired
    lateinit var itemRepository: Ch12ItemRepository

    var items: List<Ch12Item> = listOf()
    @BeforeEach
    fun init() {
        // GIVEN
        items = initItems()

        em.flush()
        em.clear()
    }

    private fun generateItemInfo(min: Int, max: Int, itemCount: Int):  List<Triple<String, Int, Int>> {
        return (0 until itemCount).map { it ->
            val price = (min..max).random()
            val stock = (1..20).random()
            val name = "item_$price"
            Triple(name, price, stock)
        }
    }

    private fun initItems(): List<Ch12Item> {
        return generateItemInfo(0, 10000, 30).map { (name, price, stock) ->
            itemRepository.save(Ch12Item(name, price, stock))
        }
    }

    @Test
    fun derived_query_interface() {
        // THEN
        val randomSavedItem = items.random()
        val randomItemName = randomSavedItem.name!!
        val randomItemStock = randomSavedItem.stockQuantity

        val result = itemRepository.findByName(randomItemName)

        // WHEN
        result.forEach { it ->
            assertEquals(randomItemStock, it.stockQuantity)
        }
    }

    @Test
    fun derived_query_class_dto() {
        // THEN
        val randomSavedItem = items.random()
        val randomItemName = randomSavedItem.name!!
        val randomItemPrice = randomSavedItem.price

        val result = itemRepository.findByPrice(randomItemPrice)

        // WHEN
        result.forEach { it ->
            assertEquals(randomItemName, it.name)
        }
    }


}