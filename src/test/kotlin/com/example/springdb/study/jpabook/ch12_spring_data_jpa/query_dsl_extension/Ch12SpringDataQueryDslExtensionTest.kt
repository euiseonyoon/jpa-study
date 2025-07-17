package com.example.springdb.study.jpabook.ch12_spring_data_jpa.query_dsl_extension

import com.example.springdb.study.jpabook.ch12_spring_data_jpa.models.Ch12Item
import com.example.springdb.study.jpabook.ch12_spring_data_jpa.repositories.Ch12ItemRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import kotlin.test.assertTrue

@DataJpaTest
class Ch12SpringDataQueryDslExtensionTest {

    @Autowired
    lateinit var itemRepository: Ch12ItemRepository
    @Autowired
    @PersistenceContext
    lateinit var em: EntityManager

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
    fun test() {
        // WHEN
        val result = itemRepository.searchItemWithMinMax(50, 9999)

        // THEN
        result.forEach {
            assertTrue { it.price >= 50 && it.price <= 9999 }
        }
    }
}
