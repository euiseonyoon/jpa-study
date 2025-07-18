package com.example.springdb.study.jpabook.ch14_collection_and_additional_features.entitiy_graph

import com.example.springdb.study.jpabook.ch14_collection_and_additional_features.models.Ch14Item
import com.example.springdb.study.jpabook.ch14_collection_and_additional_features.models.Ch14Member
import com.example.springdb.study.jpabook.ch14_collection_and_additional_features.models.Ch14Order
import com.example.springdb.study.jpabook.ch14_collection_and_additional_features.repositories.Ch14ItemRepository
import com.example.springdb.study.jpabook.ch14_collection_and_additional_features.repositories.Ch14MemberRepository
import com.example.springdb.study.jpabook.ch14_collection_and_additional_features.repositories.Ch14OrderRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import kotlin.collections.forEach
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * 엔티티 그래프의 장점
 *
 * fetch join으로 충분히 해결 할 수 있는 문제들이 많다.
 *
 * 하지만 아래처럼 원하는 기능에 달라지는 경우, 다른 JPQL을 사용해야 한다는 단점이 있다.
 *      1. 단순 Order 조회
 *      2. Order - Member 같이 조회
 *      3. Order - Item 같이 조회
 *
 * 엔티티 그래프 기능을 사용하면 Order 조회시 필요한 그래프만 추가하여 추가 정보를 가져올 수 있다.
 *
 *
 * */
@DataJpaTest
class Ch14EntityGraphTest {

    @Autowired
    @PersistenceContext
    lateinit var em: EntityManager
    @Autowired
    lateinit var memberRepository: Ch14MemberRepository
    @Autowired
    lateinit var orderRepository: Ch14OrderRepository
    @Autowired
    lateinit var itemRepository: Ch14ItemRepository

    val ORDER_COUNT = 3
    var items: List<Ch14Item> = listOf()
    var members: List<Ch14Member> = listOf()

    @BeforeEach
    fun init() {
        // GIVEN
        items = initItems()
        members = listOf("Michael", "John", "Jonathan", "Tony", "Sam").map {
            addMemer(it)
        }

        em.flush()
        em.clear()
    }

    private fun addMemer(name: String): Ch14Member {
        val member = Ch14Member()
        member.name = name
        return memberRepository.save(member)
    }

    private fun makeOrder(member: Ch14Member, items: List<Ch14Item>): Ch14Order {
        val order = Ch14Order()
        order.assignMember(member)
        items.forEach { item ->
            order.assignItem(item, ORDER_COUNT)
        }
        return orderRepository.save(order)
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
    @DisplayName("""
        em.find()를 통해 JPA(Hibernate)를 직접 이용하여 엔티티 그래프를 사용하는 예시
    """)
    fun entity_graph_jpa_hibernate_1() {
        // GIVEN
        val randomMember = members.shuffled().first()
        val randomItems = items.shuffled().take(2)
        val order = makeOrder(randomMember, randomItems)
        em.flush()
        em.clear()

        // WHEN
        val entityGraph = em.getEntityGraph("Ch14Order.withMember")
        val hints: MutableMap<String, Any> = mutableMapOf(
            "javax.persistence.fetchgraph" to entityGraph
        )
        val result = em.find(Ch14Order::class.java, order.id!!, hints)
        /**
         * 결과:
         *      Ch14Order의 member의 fetch 모드를 LAZY로 했음에도 엔티티 그래프를 사용하여 Ch14Order, Ch14Member 정보를 한번에 가져옴
         *
         *     select
         *         co1_0.id,
         *         co1_0.member_id,
         *         m1_0.id,
         *         m1_0.name
         *     from
         *         ch14order co1_0
         *     join
         *         ch14member m1_0
         *             on m1_0.id=co1_0.member_id
         *     where
         *         co1_0.id=?
         *
         * */

        // THEN
        assertEquals(randomMember.id!!, result.member!!.id)
    }

    @Test
    @DisplayName("""
        em.createQuery()를 통해 JPQL 쿼리를 만들고,
        JPA(Hibernate)를 직접 이용하여 엔티티 그래프를 사용하는 예시
    """)
    fun entity_graph_jpa_hibernate_2() {
        // GIVEN
        val randomMember = members.shuffled().first()
        val randomItems = items.shuffled().take(2)
        val order = makeOrder(randomMember, randomItems)
        em.flush()
        em.clear()

        // WHEN
        val result = em.createQuery("SELECT o FROM Ch14Order o WHERE o.id = :id")
            .setParameter("id", order.id)
            .setHint(
                "javax.persistence.fetchgraph",
                em.getEntityGraph("Ch14Order.withMember"),
            ).singleResult
        /**
         *        select
         *             co1_0.id,
         *             co1_0.member_id,
         *             m1_0.id,
         *             m1_0.name
         *         from
         *             ch14order co1_0
         *         join
         *             ch14member m1_0
         *                 on m1_0.id=co1_0.member_id
         *         where
         *             co1_0.id=?
         * */

        // THEN
        assertTrue { result is Ch14Order }
        assertEquals(randomMember.id!!, (result as Ch14Order).member!!.id)
    }

    @Test
    @DisplayName("""
        Spring Data Jpa (JpaRepository)를 통해 엔티티 그래프를 사용하는 예제
    """)
    fun entity_graph_spring_data_jpa() {
        // GIVEN
        val randomMember = members.shuffled().first()
        val randomItems = items.shuffled().take(2)
        val order = makeOrder(randomMember, randomItems)
        em.flush()
        em.clear()

        // WHEN
        val result = orderRepository.searchByIdWithMember(order.id!!)
        /**
         *        select
         *             co1_0.id,
         *             co1_0.member_id,
         *             m1_0.id,
         *             m1_0.name
         *         from
         *             ch14order co1_0
         *         join
         *             ch14member m1_0
         *                 on m1_0.id=co1_0.member_id
         *         where
         *             co1_0.id=?
         * */

        // THEN
        assertEquals(randomMember.id!!, result.member!!.id)
    }


    @Test
    @DisplayName("""
        JPA(Hibernate)를 직접 사용하여, EntityGraph + SubGraph를 사용하는 예시
        
        Ch14Order - Ch14OrderItem 까지는 Ch14Order 테이블이 관리한다
        하지만 
        Ch14OrderItem - Ch14Item 은 Ch14Order 테이블이 관리하지 않는다.
        
        따라서, Ch14Order 조회로 Ch14Order - Ch14OrderItem - Ch14Item 까지의 데이터를 한번에 가져오려면
        EntityGraph(Ch14Order-Ch14OrderItem) + SubGraph(Ch14OrderItem-Ch14Item)을 같이 사용해야 한다.
    """)
    fun entity_graph_and_sub_graph_jpa_hibernate() {
        // GIVEN
        val randomMember = members.shuffled().first()
        val randomItems = items.shuffled().take(2)
        val order = makeOrder(randomMember, randomItems)

        em.flush()
        em.clear()

        // WHEN
        val entityGraph = em.getEntityGraph("Ch14Order.withAll")
        val hints: MutableMap<String, Any> = mutableMapOf(
            "javax.persistence.fetchgraph" to entityGraph
        )
        val result = em.find(Ch14Order::class.java, order.id!!, hints)

//        em.createQuery() 사용 예시
//        val result = em.createQuery("SELECT o FROM Ch14Order o WHERE o.id = :id")
//            .setParameter("id", order.id)
//            .setHint(
//                "javax.persistence.fetchgraph",
//                em.getEntityGraph("Ch14Order.withAll"),
//            ).singleResult

        /**
         *     select
         *         co1_0.id,
         *         co1_0.member_id,
         *         m1_0.id,
         *         m1_0.name,
         *         oi1_0.order_id,
         *         oi1_0.id,
         *         oi1_0.count,
         *         oi1_0.item_id,
         *         oi1_0.price
         *         i1_0.id,
         *         i1_0.name,
         *         i1_0.price,
         *         i1_0.stock_quantity,
         *     from
         *         ch14order co1_0
         *     join
         *         ch14member m1_0
         *             on m1_0.id=co1_0.member_id
         *     left join
         *         ch14order_item oi1_0
         *             on co1_0.id=oi1_0.order_id
         *     left join
         *         ch14item i1_0
         *             on i1_0.id=oi1_0.item_id
         *     where
         *         co1_0.id=?
         *
         * */

        // THEN
        assertEquals(result.member!!.id, randomMember.id!!)
        assertEquals(
            randomItems.map { it.id!! }.toSet(),
            result.orderItems.map{ it.item!!.id!! }.toSet()
        )
    }

    @Test
    @DisplayName("""
        Spring Data Jpa (JpaRepository)를 사용하여, EntityGraph + SubGraph를 사용하는 예시
    """)
    fun entity_graph_and_sub_graph_spring_data_jpa() {
        // GIVEN
        val randomMember = members.shuffled().first()
        val randomItems = items.shuffled().take(2)
        val order = makeOrder(randomMember, randomItems)
        em.flush()
        em.clear()

        // WHEN
        val result = orderRepository.searchByIdWithAll(order.id!!)
        /**
         *     select
         *         co1_0.id,
         *         co1_0.member_id,
         *         m1_0.id,
         *         m1_0.name,
         *         oi1_0.order_id,
         *         oi1_0.id,
         *         oi1_0.count,
         *         oi1_0.item_id,
         *         i1_0.id,
         *         i1_0.name,
         *         i1_0.price,
         *         i1_0.stock_quantity,
         *         oi1_0.price
         *     from
         *         ch14order co1_0
         *     join
         *         ch14member m1_0
         *             on m1_0.id=co1_0.member_id
         *     left join
         *         ch14order_item oi1_0
         *             on co1_0.id=oi1_0.order_id
         *     left join
         *         ch14item i1_0
         *             on i1_0.id=oi1_0.item_id
         *     where
         *         co1_0.id=?
         * */

        // THEN
        assertEquals(result.member!!.id, randomMember.id!!)
        assertEquals(
            randomItems.map { it.id!! }.toSet(),
            result.orderItems.map{ it.item!!.id!! }.toSet()
        )
    }

}
