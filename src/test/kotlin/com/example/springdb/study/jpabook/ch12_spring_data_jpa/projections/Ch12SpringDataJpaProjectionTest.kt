package com.example.springdb.study.jpabook.ch12_spring_data_jpa.projections

import com.example.springdb.study.jpabook.ch12_spring_data_jpa.models.Ch12Item
import com.example.springdb.study.jpabook.ch12_spring_data_jpa.models.Ch12Member
import com.example.springdb.study.jpabook.ch12_spring_data_jpa.models.Ch12Order
import com.example.springdb.study.jpabook.ch12_spring_data_jpa.repositories.Ch12ItemRepository
import com.example.springdb.study.jpabook.ch12_spring_data_jpa.repositories.Ch12MemberRepository
import com.example.springdb.study.jpabook.ch12_spring_data_jpa.repositories.Ch12OrderItemRepository
import com.example.springdb.study.jpabook.ch12_spring_data_jpa.repositories.Ch12OrderRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import kotlin.collections.forEach
import kotlin.ranges.random
import kotlin.test.assertEquals

@DataJpaTest
class Ch12SpringDataJpaProjectionTest {

    val ORDER_COUNT = 2

    @Autowired
    @PersistenceContext
    lateinit var em: EntityManager

    @Autowired
    lateinit var itemRepository: Ch12ItemRepository

    @Autowired
    lateinit var orderRepository: Ch12OrderRepository

    @Autowired
    lateinit var memberRepository: Ch12MemberRepository

    @Autowired
    lateinit var orderItemRepository: Ch12OrderItemRepository

    var items: List<Ch12Item> = listOf()
    var members: List<Ch12Member> = listOf()

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

    private fun addMemer(name: String): Ch12Member {
        val member = Ch12Member()
        member.name = name
        return memberRepository.save(member)
    }

    private fun makeOrder(member: Ch12Member, items: List<Ch12Item>): Ch12Order {
        val order = Ch12Order()
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

    @Test
    fun derived_query_nested_interfaces() {
        // GIVEN
        val randomMember = members.random()
        val randomItems = items.shuffled().take(2)
        val madeOrder = makeOrder(randomMember, randomItems)
        em.flush()
        em.clear()

        // WHEN
        val result = orderItemRepository.findByCount(ORDER_COUNT)

        // THEN
        result.forEach { it ->
            assertEquals(randomMember.id, it.order.member.id)
        }
        val randomItemIdSet = randomItems.map { it.id }.toSet()
        val resultItemIdSet = result.map { it.item.id }.toSet()
        assertEquals(randomItemIdSet, resultItemIdSet)
        /**
         * 쿼리가 아래처럼 2번 돌았다.
         *
         * 1. OrderItem 에서 join해서 가져올 수 있는것은 한번에 조인 해서 가져왔다.
         * 2. 두번째 쿼리는 OrderItem-Order 를 가져오긴 했으나, member정보도 필요해서 따로 Member 테이블에서 따로 조회해 왔다.
         *
         * 참고: Nested Dto는 DerivedQuery에서는 안되는것 같다....
         *      org.hibernate.query.sqm.tree.expression.Compatibility
         *          Compatibility.areAssignmentCompatible()  <--- 여기에서 안되었다.
         *      예를들면, Ch10Item -> Ch10ItemDto로 형변환이 가능한지 체크하는건데, 위 함수에서 false로 나오면서 안됨
         *
         *      클래스 기반 DTO로 전환할 때는 JPA의 constructor expression이 사용
         *      내부적으로 JPA가 Tuple에서 값을 꺼내고 → DTO 생성자에 해당 값을 넣어서 생성.
         *      여기서 nested Dto면 생성자에 nested된 다른 dto의 생성자가 명시되어 있지 않아서 안됨.
         *
         * select
         *     orderItem.count,
         *     order.id,
         *     order.member_id,
         *     item.id,
         *     item.name,
         *     item.price,
         *     item.stock_quantity
         * from
         *     ch12order_item orderItem
         * left join
         *     ch12order order
         *         on order.id=orderItem.order_id
         * left join
         *     ch12item item
         *         on item.id=orderItem.item_id
         * where
         *     orderItem.count=?
         *
         *
         * select
         *     member.id,
         *     member.name
         * from
         *     ch12member member
         * where
         *     member.id=?
         *
         * */
    }

    @Test
    fun string_base_query_interface_projection() {
        val randomItem = items.random()
        val result = itemRepository.searchByNameToInterface(randomItem.name!!)

        result.forEach {
            assertEquals(randomItem.stockQuantity, it.stockQuantity)
        }
    }

    @Test
    fun string_base_query_class_dto_projection() {
        val randomItem = items.random()
        val result = itemRepository.searchByNameToDto(randomItem.name!!)

        result.forEach {
            assertEquals(randomItem.name!!, it.name)
        }
    }

}