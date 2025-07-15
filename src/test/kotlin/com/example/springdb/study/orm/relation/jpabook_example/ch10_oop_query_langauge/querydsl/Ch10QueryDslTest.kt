package com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl

import com.example.springdb.study.logger
import com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.models.Ch10Item
import com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.models.Ch10Member
import com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.models.QCh10Item
import com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.models.QCh10Member
import com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.repositories.Ch10ItemRepository
import com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.repositories.Ch10MemberRepository
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.transaction.Transactional
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import kotlin.test.assertTrue

@DataJpaTest
class Ch10QueryDslTest {

    val PRICE_CRITERIA = 100000
    val CHEAP_ITEM_COUNT = 10
    val EXPENSIVE_ITEM_COUNT = 20
    enum class OrderingFlow {
        ASC,
        DESC
    }

    private val log = logger()

    @Autowired
    @PersistenceContext
    lateinit var em: EntityManager
    @Autowired
    lateinit var memberRepository: Ch10MemberRepository
    @Autowired
    lateinit var itemRepository: Ch10ItemRepository

    @BeforeEach
    fun init() {
        // GIVEN
        listOf("Michael", "John", "Jonathan", "Tony", "Sam").forEach {
            addMemer(it)
        }

        // GIVEN
        initItems()
    }

    private fun addMemer(name: String) {
        val member = Ch10Member()
        member.name = name

        val savedMember = memberRepository.save(member)
    }

    private fun initItems() {
        val cheapItemInfos = generateItemInfo(0, PRICE_CRITERIA, CHEAP_ITEM_COUNT)
        val expensiveItemInfos = generateItemInfo(PRICE_CRITERIA, PRICE_CRITERIA + 200000, EXPENSIVE_ITEM_COUNT)

        (cheapItemInfos + expensiveItemInfos).forEach { (name, price, stock) ->
            addItem(name, price, stock)
        }
    }

    private fun generateItemInfo(min: Int, max: Int, itemCount: Int):  List<Triple<String, Int, Int>> {
        return (0 until itemCount).map { it ->
            val price = (min..max).random()
            val stock = (10..20).random()
            val name = "item_$price"
            Triple(name, price, stock)
        }
    }

    private fun addItem(name: String, price: Int, stock: Int) {
        val item = Ch10Item(name, price, stock)
        itemRepository.save(item)
    }

    @Test
    fun simple() {
        // WHEN
        val queryType = QCh10Member.ch10Member
        var predicate = queryType.name.eq("John")
        // THEN
        val john = memberRepository.findOne(predicate)
        assertTrue { john.isPresent }
        /**
         * select
         *     cm1_0.id,
         *     cm1_0.name
         * from
         *     ch10member cm1_0
         * where
         *     cm1_0.name="John"
         * fetch
         *     first ? rows only
         * */

        predicate = queryType.name.startsWithIgnoreCase("jo")
            .and(queryType.name.endsWithIgnoreCase("an"))

        val jonathan = memberRepository.findOne(predicate)
        assertTrue { jonathan.isPresent }

        /**
         * select
         *     cm1_0.id,
         *     cm1_0.name
         * from
         *     ch10member cm1_0
         * where
         *     lower(cm1_0.name) like ? escape '!'
         *     and lower(cm1_0.name) like ? escape '!'
         * fetch
         *     first ? rows only
         *
         * */
    }

    private fun checkOrdering(numbers: List<Int>, ordering: OrderingFlow): Boolean {
        if (numbers.size <= 1) return true

        for (index in 0 until numbers.lastIndex) {
            val current = numbers[index]
            val next = numbers[index + 1]

            if (ordering == OrderingFlow.ASC) {
                if (current > next) {
                    return false
                }
            } else {
                if (current < next) {
                    return false
                }
            }
        }
        return true
    }

    @Transactional
    @Test
    fun paging_and_sorting() {
        val page = 0L
        val perPage = 10L

        val query = JPAQueryFactory(em)
        val item = QCh10Item.ch10Item

        val result: List<Ch10Item> = query.select(item)
            .from(item)
            .where(item.price.loe(PRICE_CRITERIA))
            .orderBy(item.price.desc(), item.stockQuantity.asc())
            .offset(page).limit(perPage)
            .fetch()

        /**
         * JPQL:
         *
         * select
         *     ch10Item
         * from
         *     Ch10Item ch10Item
         * where
         *     ch10Item.price <= ?1
         * order by
         *     ch10Item.price desc,
         *     ch10Item.stockQuantity asc
         *
         * SQL :
         *
         * select
         *     ci1_0.id,
         *     ci1_0.name,
         *     ci1_0.price,
         *     ci1_0.stock_quantity
         * from
         *     ch10item ci1_0
         * where
         *     ci1_0.price<=100000
         * order by
         *     ci1_0.price desc,
         *     ci1_0.stock_quantity
         * offset
         *     0 rows
         * fetch
         *     first 10 rows only*/

        assertTrue { result.size <= perPage }
        assertTrue {
            checkOrdering(result.map { it.price }, OrderingFlow.DESC)
        }
    }

    @Transactional
    @Test
    fun grouping() {
        val query = JPAQueryFactory(em)
        val item = QCh10Item.ch10Item
        val result = query.select(item)
            .from(item)
            .groupBy(item.price) // price로 그룹
            .having(item.price.goe(PRICE_CRITERIA)) // 그룹 기준(price)가 PRICE_CRITERIA 보다 크거나 같음
            .fetch()
        /**
         * JPQL:
         *
         * select
         *     ch10Item
         * from
         *     Ch10Item ch10Item
         * group by
         *     ch10Item.price
         * having
         *     ch10Item.price >= 100000
         *
         *
         * SQL:
         *
         * select
         *     ci1_0.id,
         *     ci1_0.name,
         *     ci1_0.price,
         *     ci1_0.stock_quantity
         * from
         *     ch10item ci1_0
         * group by
         *     ci1_0.price
         * having
         *     ci1_0.price>=100000
         * */

        val prices = result.map { it.price }.toSet()
        assertTrue { prices.size == result.size }
        prices.forEach {
            assertTrue { it >= PRICE_CRITERIA }
        }
    }

}
