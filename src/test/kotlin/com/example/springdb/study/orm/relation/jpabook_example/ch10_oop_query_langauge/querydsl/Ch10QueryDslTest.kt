package com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl

import com.example.springdb.study.logger
import com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.models.Ch10Item
import com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.models.Ch10Member
import com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.models.Ch10Order
import com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.models.Ch10OrderItem
import com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.models.QCh10Item
import com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.models.QCh10Member
import com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.models.QCh10Order
import com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.models.QCh10OrderItem
import com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.repositories.Ch10ItemRepository
import com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.repositories.Ch10MemberRepository
import com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.repositories.Ch10OrderItemRepository
import com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.repositories.Ch10OrderRepository
import com.querydsl.core.Tuple
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.transaction.Transactional
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import kotlin.test.assertTrue

@DataJpaTest
class Ch10QueryDslTest {

    val PRICE_CRITERIA = 100000
    val CHEAP_ITEM_COUNT = 10
    val EXPENSIVE_ITEM_COUNT = 20
    val ORDER_COUNT = 2

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
    @Autowired
    lateinit var orderRepository: Ch10OrderRepository
    @Autowired
    lateinit var orderItemRepository: Ch10OrderItemRepository

    @BeforeEach
    fun init() {
        // GIVEN
        val members = listOf("Michael", "John", "Jonathan", "Tony", "Sam").map {
            addMemer(it)
        }

        // GIVEN
        val items = initItems()

        // GIVEN
        val randomOrder = makeOrder(members.random(), items.random())
    }

    private fun makeOrder(member: Ch10Member, item: Ch10Item): Ch10Order {
        val order = Ch10Order()
        order.assignMember(member)
        order.assignItem(item, ORDER_COUNT)
        return orderRepository.save(order)
    }

    private fun addMemer(name: String): Ch10Member {
        val member = Ch10Member()
        member.name = name

        return memberRepository.save(member)
    }

    private fun initItems(): List<Ch10Item> {
        val cheapItemInfos = generateItemInfo(0, PRICE_CRITERIA, CHEAP_ITEM_COUNT)
        val expensiveItemInfos = generateItemInfo(PRICE_CRITERIA, PRICE_CRITERIA + 200000, EXPENSIVE_ITEM_COUNT)

        return (cheapItemInfos + expensiveItemInfos).map { (name, price, stock) ->
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

    private fun addItem(name: String, price: Int, stock: Int): Ch10Item {
        val item = Ch10Item(name, price, stock)
        return itemRepository.save(item)
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

    @Test
    fun join_basic() {
        val query = JPAQueryFactory(em)
        val order = QCh10Order.ch10Order
        val member = QCh10Member.ch10Member
        val orderItem = QCh10OrderItem.ch10OrderItem

        // Ch10Order의 정보만 가져옴
        val result = query
            .from(order)
            .join(order.member, member)
            .leftJoin(order.orderItems, orderItem)
            .fetch()

        assertTrue { result.size == 1 }
        /**
         * JPQL:
         *
         * select
         *     ch10Order
         * from
         *     Ch10Order ch10Order
         * inner join
         *     ch10Order.member as ch10Member
         * left join
         *     ch10Order.orderItems as ch10OrderItem
         * */
    }

    @Test
    fun join_select_multiple() {
        val query = JPAQueryFactory(em)
        val order = QCh10Order.ch10Order
        val member = QCh10Member.ch10Member
        val orderItem = QCh10OrderItem.ch10OrderItem
        val item = QCh10Item.ch10Item

        // Ch10Order, Ch10Member 정보 가져옴
        val result1 = query
            .select(order, member)
            .from(order)
            .join(order.member, member)
            .leftJoin(order.orderItems, orderItem)
            .fetch()
        /**
         * JPQL:
         *
         * select
         *     ch10Order,
         *     ch10Member
         * from
         *     Ch10Order ch10Order
         * inner join
         *     ch10Order.member as ch10Member
         * left join
         *     ch10Order.orderItems as ch10OrderItem
         *
         * SQL:
         *
         * select
         *     co1_0.id,
         *     co1_0.member_id,
         *     m1_0.id,
         *     m1_0.name
         * from
         *     ch10order co1_0
         * join
         *     ch10member m1_0 on m1_0.id=co1_0.member_id
         * left join
         *     ch10order_item oi1_0 on co1_0.id=oi1_0.order_id
         * */


        val result2 = query
            .select(order, member, item)
            .from(order)
            .join(order.member, member)
            .leftJoin(order.orderItems, orderItem)
            .leftJoin(orderItem.item, item)
            .fetch()
        /**
         * select
         *     ch10Order,
         *     ch10Member,
         *     ch10Item
         * from
         *     Ch10Order ch10Order
         * inner join
         *     ch10Order.member as ch10Member
         * left join
         *     ch10Order.orderItems as ch10OrderItem
         * left join
         *     ch10OrderItem.item as ch10Item
         *
         *
         * select
         *     co1_0.id,
         *     co1_0.member_id,
         *     m1_0.id,
         *     m1_0.name,
         *     i1_0.id,
         *     i1_0.name,
         *     i1_0.price,
         *     i1_0.stock_quantity
         * from
         *     ch10order co1_0
         * join
         *     ch10member m1_0 on m1_0.id=co1_0.member_id
         * left join
         *     ch10order_item oi1_0 on co1_0.id=oi1_0.order_id
         * left join
         *     ch10item i1_0 on i1_0.id=oi1_0.item_id
         *
         * */
    }

    @Test
    fun join_on() {
        val query = JPAQueryFactory(em)

        val order = QCh10Order.ch10Order
        val orderItem = QCh10OrderItem.ch10OrderItem

        val result1 = query
            .select(order, orderItem)
            .from(order)
            .leftJoin(order.orderItems, orderItem)
            .on(orderItem.count.lt(ORDER_COUNT))
            .fetch()
        /**
         * select
         *     ch10Order,
         *     ch10OrderItem
         * from
         *     Ch10Order ch10Order
         * left join
         *     ch10Order.orderItems as ch10OrderItem with ch10OrderItem.count < 2
         *
         *
         * select
         *     co1_0.id,
         *     co1_0.member_id,
         *     oi1_0.id,
         *     oi1_0.count,
         *     oi1_0.item_id,
         *     oi1_0.order_id,
         *     oi1_0.price
         * from
         *     ch10order co1_0
         * left join
         *     ch10order_item oi1_0 on co1_0.id=oi1_0.order_id and oi1_0.count<2
         * */
        result1.forEach { it: Tuple ->
            val order = it.get(0, Ch10Order::class.java)
            val orderItem = it.get(1, Ch10OrderItem::class.java)
            assertNotNull(order)
            assertNull(orderItem)
        }

        val result2 = query
            .select(order, orderItem)
            .from(order)
            .leftJoin(order.orderItems, orderItem)
            .on(orderItem.count.goe(ORDER_COUNT))
            .fetch()

        result2.forEach { it: Tuple ->
            val order = it.get(0, Ch10Order::class.java)
            val orderItem = it.get(1, Ch10OrderItem::class.java)
            assertNotNull(order)
            assertNotNull(orderItem)
        }
    }

    @Test
    fun join_fetch_join() {
        val query = JPAQueryFactory(em)

        val order = QCh10Order.ch10Order
        val member = QCh10Member.ch10Member
        val orderItem = QCh10OrderItem.ch10OrderItem

        val result1 = query
            .from(order)
            .innerJoin(order.member, member).fetchJoin()
            .leftJoin(order.orderItems, orderItem).fetchJoin()
            .fetch()

        /**
         * select
         *     ch10Order
         * from
         *     Ch10Order ch10Order
         * inner join
         *     fetch ch10Order.member as ch10Member
         * left join
         *     fetch ch10Order.orderItems as ch10OrderItem
         *
         * select
         *     co1_0.id,
         *     co1_0.member_id,
         *     m1_0.id,
         *     m1_0.name,
         *     oi1_0.order_id,
         *     oi1_0.id,
         *     oi1_0.count,
         *     oi1_0.item_id,
         *     oi1_0.price
         * from
         *     ch10order co1_0
         * join
         *     ch10member m1_0 on m1_0.id=co1_0.member_id
         * left join
         *     ch10order_item oi1_0 on co1_0.id=oi1_0.order_id
         * */
    }

    @Test
    @DisplayName("""
        from에 여러 엔티티를 넣어서 세타조인(theta join)을 한다.
        세타조인: A theta join, also known as a conditional join, is a join operation in relational algebra 
                that combines tuples from two or more tables based on a specified condition
                
        * 사실 아래의 예시는 더 디테일하게 말하면 equijoin이다.
          세타조인은 이퀴조인을 포함한다.  (집합에서 꽃과 해바라기 처럼 ㅎㅎㅎ)
    """)
    fun join_theta_join() {
        val query = JPAQueryFactory(em)

        val order = QCh10Order.ch10Order
        val member = QCh10Member.ch10Member

        val result = query.from(order, member)
            .where(order.member.eq(member))
            .fetch()
        /**
         * select
         *     ch10Order
         * from
         *     Ch10Order ch10Order,
         *     Ch10Member ch10Member
         * where
         *     ch10Order.member = ch10Member
         *
         * select
         *     co1_0.id,
         *     co1_0.member_id
         * from
         *     ch10order co1_0,
         *     ch10member cm1_0
         * where
         *     co1_0.member_id=cm1_0.id
         * */
    }

}
