package com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl

import com.example.springdb.study.logger
import com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.models.Ch10Item
import com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.models.Ch10Member
import com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.models.Ch10Order
import com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.models.Ch10OrderItem
import com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.models.QCh10Item
import com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.models.QCh10Item.ch10Item
import com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.models.QCh10ItemDto
import com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.models.QCh10Member
import com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.models.QCh10Order
import com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.models.QCh10OrderItem
import com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.repositories.Ch10ItemRepository
import com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.repositories.Ch10MemberRepository
import com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.repositories.Ch10OrderItemRepository
import com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.repositories.Ch10OrderRepository
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.Tuple
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import com.querydsl.jpa.impl.JPAUpdateClause
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
    val MIN_STOCK_QUANTITY = 5

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

    var items: List<Ch10Item> = listOf()
    var members: List<Ch10Member> = listOf()

    @BeforeEach
    fun init() {
        // GIVEN
        members = listOf("Michael", "John", "Jonathan", "Tony", "Sam").map {
            addMemer(it)
        }

        // GIVEN
        items = initItems()
    }

    private fun makeOrder(member: Ch10Member, items: List<Ch10Item>): Ch10Order {
        val order = Ch10Order()
        order.assignMember(member)
        items.forEach { item ->
            order.assignItem(item, ORDER_COUNT)
        }
        return orderRepository.save(order)
    }

    private fun addMemer(name: String): Ch10Member {
        val member = Ch10Member()
        member.name = name

        return memberRepository.save(member)
    }

    private fun initItems(): List<Ch10Item> {
        val cheapItemInfos = generateItemInfo(0, PRICE_CRITERIA - 1, CHEAP_ITEM_COUNT)
        val expensiveItemInfos = generateItemInfo(PRICE_CRITERIA, PRICE_CRITERIA + 200000, EXPENSIVE_ITEM_COUNT)

        return (cheapItemInfos + expensiveItemInfos).map { (name, price, stock) ->
            addItem(name, price, stock)
        }
    }

    private fun generateItemInfo(min: Int, max: Int, itemCount: Int):  List<Triple<String, Int, Int>> {
        return (0 until itemCount).map { it ->
            val price = (min..max).random()
            val stock = (MIN_STOCK_QUANTITY..20).random()
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

        // WHEN
        predicate = queryType.name.startsWithIgnoreCase("jo")
            .and(queryType.name.endsWithIgnoreCase("an"))

        // THEN
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

        // WHEN
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

        // THEN
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

        // WHEN
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

        // THEN
        val prices = result.map { it.price }.toSet()
        assertTrue { prices.size == result.size }
        prices.forEach {
            assertTrue { it >= PRICE_CRITERIA }
        }
    }

    @Test
    fun join_basic() {
        // GIVEN
        makeOrder(members.random(), listOf(items.random()))
        em.flush()
        em.clear()

        val query = JPAQueryFactory(em)
        val order = QCh10Order.ch10Order
        val member = QCh10Member.ch10Member
        val orderItem = QCh10OrderItem.ch10OrderItem

        // WHEN : Ch10Order의 정보만 가져옴
        val result = query
            .from(order)
            .join(order.member, member)
            .leftJoin(order.orderItems, orderItem)
            .fetch()

        // THEN
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
        // GIVEN
        makeOrder(members.random(), listOf(items.random()))
        em.flush()
        em.clear()

        val query = JPAQueryFactory(em)
        val order = QCh10Order.ch10Order
        val member = QCh10Member.ch10Member
        val orderItem = QCh10OrderItem.ch10OrderItem
        val item = QCh10Item.ch10Item

        // WHEN : Ch10Order, Ch10Member 정보 가져옴
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

        // WHEN
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
        makeOrder(members.random(), listOf(items.random()))
        em.flush()
        em.clear()

        val query = JPAQueryFactory(em)

        val order = QCh10Order.ch10Order
        val orderItem = QCh10OrderItem.ch10OrderItem

        // WHEN
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
        // THEN
        result1.forEach { it: Tuple ->
            val order = it.get(0, Ch10Order::class.java)
            val orderItem = it.get(1, Ch10OrderItem::class.java)
            assertNotNull(order)
            assertNull(orderItem)
        }

        // WHEN
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
        // GIVEN
        makeOrder(members.random(), listOf(items.random()))
        em.flush()
        em.clear()

        val query = JPAQueryFactory(em)

        val order = QCh10Order.ch10Order
        val member = QCh10Member.ch10Member
        val orderItem = QCh10OrderItem.ch10OrderItem

        // WHEN
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
        // GIVEN
        makeOrder(members.random(), listOf(items.random()))
        em.flush()
        em.clear()

        val query = JPAQueryFactory(em)

        val order = QCh10Order.ch10Order
        val member = QCh10Member.ch10Member

        // WHEN
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

    @Test
    fun sub_query_single() {
        val query = JPAQueryFactory(em)

        val item = QCh10Item.ch10Item
        val itemSub = QCh10Item("itemSub")

        // WHEN
        val maxPriceSubQuery = JPAExpressions
            .select(itemSub.price.max())
            .from(itemSub)
            // 책에 나와있는 unique()는 QueryDsl 5.X 이후로 없어짐

        val result = query.from(item)
            .where(item.price.eq(maxPriceSubQuery))
            .fetchOne()
        val mostExpensiveItem = items.maxByOrNull { it.price }

        // THEN
        assertTrue { result == mostExpensiveItem }

        /**
         * select
         *     ch10Item
         * from
         *     Ch10Item ch10Item
         * where
         *     ch10Item.price = (
         *         select
         *             max(itemSub.price)
         *         from
         *             Ch10Item itemSub
         *     )
         *
         * select
         *     ci1_0.id,
         *     ci1_0.name,
         *     ci1_0.price,
         *     ci1_0.stock_quantity
         * from
         *     ch10item ci1_0
         * where
         *     ci1_0.price=(
         *         select
         *             max(ci2_0.price)
         *         from
         *             ch10item ci2_0
         *     )
         * */

        // WHEN
        val negativePriceSubQuery = JPAExpressions
            .select(itemSub.price)
            .from(itemSub)
            .where(itemSub.price.lt(0))

        val result2 = query.from(item)
            .where(item.price.loe(negativePriceSubQuery))
            .fetchOne()

        // THEN
        assertNull(result2)
    }

    @Test
    fun sub_query_multiply() {
        // GIVEN
        val randomOrder = makeOrder(members.random(), listOf(items.random()))
        em.flush()
        em.clear()

        val randomOrderName = randomOrder.member!!.name!!
        val randomOrderMostStockItem = randomOrder.orderItems.maxByOrNull { it.item!!.stockQuantity }

        log.info("order.member.name={}", randomOrderName)
        log.info("randomOrderMostStockItem={}", randomOrderMostStockItem)

        val query = JPAQueryFactory(em)

        val orderItem = QCh10OrderItem.ch10OrderItem
        val memberSubQuery = QCh10Member("memberSubQuery")
        val itemSubQuery = QCh10Item("itemSubQuery")

        // WHEN : 주문을 시킨 사람을 찾음
        val memberNameEndsWith = JPAExpressions
            .select(memberSubQuery)
            .from(memberSubQuery)
            .where(memberSubQuery.name.endsWithIgnoreCase(randomOrderName.takeLast(2)))

        // 주문한 item중 수량이 충분한것만 찾는다. (모두 MIN_STOCK_QUANTITY 이상으로 init되어 있다.)
        val enoughStockItems = JPAExpressions
            .select(itemSubQuery)
            .from(itemSubQuery)
            .where(itemSubQuery.stockQuantity.goe(MIN_STOCK_QUANTITY))

        val result1 =query.from(orderItem)
            .where(
                orderItem.order.member.eq(memberNameEndsWith)
                    .and(orderItem.item.`in`(enoughStockItems))
            ).fetch()

        /**
         * select
         *     ch10OrderItem
         * from
         *     Ch10OrderItem ch10OrderItem
         * where
         *     ch10OrderItem.order.member = (
         *         select
         *             memberSubQuery
         *         from
         *             Ch10Member memberSubQuery
         *         where
         *             lower(memberSubQuery.name) like ?1 escape '!'
         *     )
         *     and ch10OrderItem.item in (
         *         select
         *             itemSubQuery
         *         from
         *             Ch10Item itemSubQuery
         *         where
         *             itemSubQuery.stockQuantity >= ?2
         *     )
         *
         *
         * select
         *     coi1_0.id,
         *     coi1_0.count,
         *     coi1_0.item_id,
         *     coi1_0.order_id,
         *     coi1_0.price
         * from
         *     ch10order_item coi1_0
         * join
         *     ch10order o1_0
         *         on o1_0.id=coi1_0.order_id
         * where
         *     o1_0.member_id=(
         *         select
         *             cm1_0.id
         *         from
         *             ch10member cm1_0
         *         where
         *             lower(cm1_0.name) like ? escape '!'
         *     )
         *     and coi1_0.item_id in (
         *         select
         *             ci1_0.id
         *         from
         *             ch10item ci1_0
         *         where
         *             ci1_0.stock_quantity>=?
         *     )
         * */
    }


    @Test
    @DisplayName("""
        프로젝션의 대상이 하나면, 해당 타입으로 반환한다. ( 아래는 프로젝션의 대상이 Ch10Item.name(String) )
    """)
    fun projection_simple() {
        val query = JPAQueryFactory(em)

        val item = QCh10Item.ch10Item

        val result = query
            .select(item.name)
            .from(item)
            .fetch()

        result.forEach {
            assertTrue { it is String }
        }
    }

    @Test
    fun projection_tuple1() {
        val query = JPAQueryFactory(em)

        val item = QCh10Item.ch10Item

        val result: List<Tuple> = query
            .select(item.name, item.price)
            .from(item)
            .fetch()

        result.forEach { tuple ->
            val name: String? = tuple.get(item.name)
            val price: Int? = tuple.get(item.price)
            // val name = tuple.get(0, String::class.java)
            // val price = tuple.get(1, Int::class.java)
            assertTrue { name is String }
            assertTrue { price is Int }
        }
    }

    @Test
    fun projection_tuple2() {
        // GIVEN
        val RANDOM1_ITEM_COUNT = 2
        val RANDOM2_ITEM_COUNT = 1

        val randomMember1 = members.random()
        val randomMember2 = (members - randomMember1).random()
        val randomOrder1 = makeOrder(randomMember1, items.shuffled().take(RANDOM1_ITEM_COUNT))
        val randomOrder2 = makeOrder(randomMember2, items.shuffled().take(RANDOM2_ITEM_COUNT))

        em.persist(randomOrder1)
        em.persist(randomOrder2)
        em.flush()
        em.clear()

        val query = JPAQueryFactory(em)

        val order = QCh10Order.ch10Order
        val orderItem = QCh10OrderItem.ch10OrderItem
        val item = QCh10Item.ch10Item
        val member = QCh10Member.ch10Member

        // WHEN
        val result = query
            .select(order, item)
            .from(order)
            .join(order.member, member).fetchJoin()
            .leftJoin(order.orderItems, orderItem)
            .leftJoin(orderItem.item, item)
            .fetch()

        /**
         * select
         *     ch10Order,
         *     ch10Item
         * from
         *     Ch10Order ch10Order
         * inner join
         *
         * fetch
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
         * */

        // THEN
        val memberToItemsMap: Map<Long, List<Ch10Item>> = result
            .groupBy { tuple ->
                tuple.get(order)?.member!!.id!!
            }
            .mapValues { entry: Map.Entry<Long, List<Tuple>> ->
                entry.value.mapNotNull { it.get(item) }
            }

        assertTrue { memberToItemsMap.keys.contains(randomMember1.id) }
        assertTrue { memberToItemsMap.keys.contains(randomMember2.id) }

        assertTrue { memberToItemsMap[randomMember1.id]?.size == RANDOM1_ITEM_COUNT }
        assertTrue { memberToItemsMap[randomMember2.id]?.size == RANDOM2_ITEM_COUNT }
    }

    @Test
    fun projection_dto() {
        val query = JPAQueryFactory(em)

        val item = QCh10Item.ch10Item

        // WHEN
        val result = query
            .select(QCh10ItemDto(item.name, item.price, item.stockQuantity ))
            .from(item)
            .fetch()

        // THEN
        val (cheapItems, expensiveItems) = result.partition { it.price < PRICE_CRITERIA }

        assertTrue { cheapItems.size == CHEAP_ITEM_COUNT }
        assertTrue { expensiveItems.size == EXPENSIVE_ITEM_COUNT }
    }

    @Test
    fun update_delete_batch_query() {
        // GIVEN
        val item = QCh10Item.ch10Item
        val query = JPAQueryFactory(em)
        val updateClause = JPAUpdateClause(em, item)
        // 삭제 작업시 deleteClause 사용
        // val deleteClause = JPADeleteClause(em, item)

        // WHEN
        val oldResult = query.select(item).from(item).where(item.price.loe(PRICE_CRITERIA)).fetch()
        assertTrue { oldResult.size == CHEAP_ITEM_COUNT }

        val updatedCount = updateClause
            .where(item.price.loe(PRICE_CRITERIA))
            .set(item.price, item.price.add(PRICE_CRITERIA))
            .execute()

        // THEN
        assertTrue { updatedCount == CHEAP_ITEM_COUNT.toLong() }
        val result = query.from(item).where(item.price.loe(PRICE_CRITERIA)).fetch()
        assertTrue { result.isEmpty() }

        /**
         * 주의:
         *    QueryDSL을 통해 수정, 삭제 같은 배치쿼리를 수행하면, 영속성 컨텍스트를 무시하고 바로 DB에 직접 쿼리를 적용한다.
         *    (JPQL 배치 쿼리도 똑같음)
         *
         *  예:
         *          val item = itemRepository.findById(1L).get()
         *          item.price = 200
         *
         *          // QueryDSL로 배치 업데이트
         *          queryFactory.update(QCh10Item.ch10Item)
         *              .set(QCh10Item.ch10Item.price, 9999)
         *              .where(QCh10Item.ch10Item.id.eq(1L))
         *              .execute()
         *
         *          em.flush()  <--- 여기서 문제 발생
         *          em.clear()
         *
         *  이렇게 되면, 가격이   DB에서: 기존가격 --> 9999 --> 200(flush() 때문에 다시 업데이트)으로 된다.
         *
         *  결론:
         *      1. 배치성 작업 후, em.clear()를 해서 영속성 캐시(1차 캐시)를 정리해주는것도 좋다.
         *      2. em.flush()는 배치성 작업 후에 하지 않고, 해야하려면 배치성 작업 전에 해야할것 같다.
         *      3. 검색성 쿼리는 배치성 작업 후에 하는것이 적당하다.
         * */
    }

    @Test
    fun dynamic_query() {
        val query = JPAQueryFactory(em)
        val item = QCh10Item.ch10Item

        // WHEN1
        val builder = BooleanBuilder()
        builder.and(item.price.goe(PRICE_CRITERIA))
        builder.and(item.stockQuantity.lt(MIN_STOCK_QUANTITY))

        // THEN1
        assertTrue { query.from(item).where(builder).fetch().isEmpty() }

        // WHEN2
        val builder2 = BooleanBuilder()
        builder2.and(item.price.loe(PRICE_CRITERIA))
        builder2.and(item.stockQuantity.goe(MIN_STOCK_QUANTITY))
        val result = query.select(item).from(item).where(builder2).fetch()

        // THEN2
        result.forEach { item ->
            assertTrue { item.price <= PRICE_CRITERIA && item.stockQuantity >= MIN_STOCK_QUANTITY}
        }
    }

    @Test
    fun method_delegation() {
        val query = JPAQueryFactory(em)
        val item = ch10Item

        // WHEN
        val result = query
            .select(item)
            .from(item)
            .where(getCheapPriceItem(item, PRICE_CRITERIA), getEnoughStockedItem(item, MIN_STOCK_QUANTITY))
            .fetch()

        // THEN
        result.forEach { item ->
            assertTrue { item.price <= PRICE_CRITERIA && item.stockQuantity >= MIN_STOCK_QUANTITY}
        }
    }

    private fun getCheapPriceItem(item: QCh10Item?, price: Int): BooleanExpression? {
        return if (item != null) {
            item.price.loe(price)
        } else {
            null
        }
    }

    private fun getEnoughStockedItem(item: QCh10Item?, minStock: Int): BooleanExpression? {
        return if (item != null) {
            item.stockQuantity.goe(minStock)
        } else {
            null
        }
    }
}
