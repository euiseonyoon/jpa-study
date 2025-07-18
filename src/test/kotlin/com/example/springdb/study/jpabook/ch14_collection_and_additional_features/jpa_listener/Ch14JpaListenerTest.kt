package com.example.springdb.study.jpabook.ch14_collection_and_additional_features.jpa_listener

import com.example.springdb.study.jpabook.ch14_collection_and_additional_features.models.Ch14Item
import com.example.springdb.study.jpabook.ch14_collection_and_additional_features.repositories.Ch14ItemRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import kotlin.test.BeforeTest

@DataJpaTest
class Ch14JpaListenerTest {

    @Autowired
    @PersistenceContext
    lateinit var em: EntityManager

    @Autowired
    lateinit var itemRepository: Ch14ItemRepository


    var items: List<Ch14Item> = listOf()

    @BeforeTest
    fun init() {
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

    private fun initItems(): List<Ch14Item> {
        return generateItemInfo(0, 10000, 30).map { (name, price, stock) ->
            itemRepository.save(Ch14Item(name, price, stock))
        }
    }


    @Test
    fun test() {
        val newItem = Ch14Item("MyItem", 100, 100)

        itemRepository.save(newItem)
        // 예상: PrePersist, PostPersist
        // 결과: 예상과 동일
        em.flush()
        em.clear()

        em.merge(newItem)
        // 예상: PrePersist
        // 결과: PostLoad
        em.clear()

        val namedMyItem = itemRepository.findByName("MyItem")
        namedMyItem.forEach {
            it.price = 200
            itemRepository.save(it)
        }
        em.flush()
        em.clear()
        // 예상: PostLoad, PreUpdate, PostUpdate
        // 결과: 예상과 동일

        em.merge(newItem)
        itemRepository.delete(newItem)
        em.flush()
        em.clear()
        // 예상: PrePersist, PreRemove, PostRemove
        // 결과: PostLoad, PreRemove, PostRemove

        val newItem2 = Ch14Item("MyItem2", 200, 200)
        em.merge(newItem2)
        // 예상: PrePersist
        // 결과: 예상과 동일

        /**
         * 결과:
         *
         *      대부분 생각한 데로 였나, em.clear()후에 merge를 다시 해도
         *      PrePersist가 아닌 PostLoad가 발생, 이유는?
         *      영속성 컨텍스트 자체에서 없어서 DB로 부터 검색을 했다.
         *      merge()가 PrePersist를 발생시키는건, 새로운 엔티티를 merge() 할때!!
         * */
    }

}