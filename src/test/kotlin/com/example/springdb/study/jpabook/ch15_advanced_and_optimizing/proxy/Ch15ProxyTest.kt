package com.example.springdb.study.jpabook.ch15_advanced_and_optimizing.proxy

import com.example.springdb.study.jpabook.ch15_advanced_and_optimizing.models.Ch15Book
import com.example.springdb.study.jpabook.ch15_advanced_and_optimizing.models.Ch15Item
import com.example.springdb.study.jpabook.ch15_advanced_and_optimizing.models.Ch15Member
import com.example.springdb.study.jpabook.ch15_advanced_and_optimizing.models.Ch15Order
import com.example.springdb.study.jpabook.ch15_advanced_and_optimizing.models.Ch15OrderItem
import com.example.springdb.study.logger
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.hibernate.Hibernate
import org.hibernate.proxy.HibernateProxy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@DataJpaTest
class Ch15ProxyTest {
    private val log = logger()

    @Autowired
    @PersistenceContext
    lateinit var em: EntityManager

    @Test
    fun proxy_test1() {
        // GIVEN
        val member = Ch15Member("Member1")
        em.persist(member)
        em.flush()
        em.clear()

        val refMember = em.getReference(Ch15Member::class.java, member.id!!)
        val findMember = em.find(Ch15Member::class.java, member.id!!)

        log.info("refMember={}", refMember.javaClass)
        log.info("findMember={}", findMember.javaClass)
        /**
         * 상황:
         *   0. em.clear()로 해당 스레드의 영속성 컨텍스트에 아무것도 없음
         *   1. getReference()로 proxy를 먼저 조회
         *   2. 이후 find()로 같은 ID의 엔티티 조회
         *
         * 결과:
         *   1. getReference(): 프록시 객체 생성, 1차 캐시에 저장 (DB Query 없음)
         *   2. em.find(): 1차 캐시에서 프록시를 반환
         *     2.1 프록시가 아직 초기화되지 않았음( target(엔티티) = null ), 초기화 요청
         *     2.2 내부적으로 DB 조회 Query 발생 (실제 엔티티 로딩), 조회된 엔티티는 영속성 컨텍스트에 생성
         *     2.3 프록시가 해당 target(엔티티)를 참조하도록 초기화됨
         *
         * LOG:
         *
         * c.e.s.s.j.ch15.proxy.Ch15ProxyTest       : refMember=class com.example.springdb.study.jpabook.ch15.models.Ch15Member$HibernateProxy
         * c.e.s.s.j.ch15.proxy.Ch15ProxyTest       : findMember=class com.example.springdb.study.jpabook.ch15.models.Ch15Member$HibernateProxy
         *
         * */
        assertTrue { refMember == findMember }
    }

    @Test
    fun proxy_test2() {
        // GIVEN
        val member = Ch15Member("Member1")
        em.persist(member)
        em.flush()
        em.clear()

        // proxy_test1의 순서만 바꿈
        val findMember = em.find(Ch15Member::class.java, member.id!!)
        val refMember = em.getReference(Ch15Member::class.java, member.id!!)

        log.info("refMember={}", refMember.javaClass)
        log.info("findMember={}", findMember.javaClass)

        /**
         * 상황:
         *   0. em.clear()로 해당 스레드의 영속성 컨텍스트에 아무것도 없음
         *   1. em.find()로 엔티티 조회
         *   2. 이후 em.getReference()로 같은 ID의 엔티티 조회
         *
         * 결과:
         *   1. em.find():
         *      1.1 영속성 컨텍스트에 id를 가지 Ch15Member 엔티티 있나 확인
         *      1.2 없으므로 DB 조회
         *      1.3 영속성 컨텍스트에 id를 가진 Ch15Member 엔티티 등록
         *   2. em.getReference()
         *      2.1 영속성 컨텍스트에 id를 가진 엔티티 이미 있음
         *      2.2 프록시 만들 필요 없음
         *      2.3 영속성 컨텍스트에 있던 엔티티 반환
         *
         * c.e.s.s.j.ch15.proxy.Ch15ProxyTest       : refMember=class com.example.springdb.study.jpabook.ch15.models.Ch15Member
         * c.e.s.s.j.ch15.proxy.Ch15ProxyTest       : findMember=class com.example.springdb.study.jpabook.ch15.models.Ch15Member
         *
         * */
        assertTrue { refMember == findMember }
    }

    @Test
    fun proxy_type_comparison() {
        // GIVEN
        val member = Ch15Member("Member1")
        em.persist(member)
        em.flush()
        em.clear()

        val refMember = em.getReference(Ch15Member::class.java, member.id!!)

        // refMember.javaClass 는 Ch15Member를 상속받은 HibernateProxy 이다.
        assertFalse { refMember.javaClass == Ch15Member::class.java }
        assertTrue { refMember is Ch15Member }
    }

    @Test
    @DisplayName("엔티티의 equals()를 구현하여 비교 테스트를 해봅니다.")
    fun entity_proxy_equality_comparison() {
        // GIVEN
        val member = Ch15Member("Member1")
        em.persist(member)
        em.flush()
        em.clear()

        val memberNotPersisted = Ch15Member("Member1")
        val refMember = em.getReference(Ch15Member::class.java, member.id!!)

        // THEN
        // id가 없거나 다르면 false
        assertFalse { refMember.equals(memberNotPersisted) }
        // 프록시의 target(엔티티)가 초기화 되지 않았기 때문에 false
        assertFalse { memberNotPersisted.equals(refMember) }

        // 프록시의 target(엔티티) 초기화
        val findMember = em.find(Ch15Member::class.java, member.id!!)
        assertTrue { findMember.equals(refMember) }
    }

    @Test
    @DisplayName("부모타입으로 프록시 조회")
    fun proxy_search_by_parent_type() {
        // GIVEN
        val book = Ch15Book()
        book.name = "Some Book1"
        book.isbn = "12345.abcde"
        book.author = "John"
        book.price = 100
        book.stockQuantity = 20

        em.persist(book)
        em.flush()
        em.clear()

        // WHEN
        val itemProxy = em.getReference(Ch15Item::class.java, book.id!!)

        // THEN
        assertFalse { itemProxy is Ch15Book }
        assertTrue { itemProxy is Ch15Item }
        assertFalse { itemProxy.javaClass == Ch15Book::class.java }
        assertThrows<ClassCastException> { itemProxy as Ch15Book }

        /**
         * 여기서 문제는 itemProxy는 Ch15Item 타입을 타겟으로 하는 프록시이다.
         * 그래서 Ch15Book 으로 다운케스팅 하려고 한다면 문제가 발생한다.
         * */
    }

    @Test
    @DisplayName("상속관계와 프록시 도메인 모델")
    fun inheritance_and_proxy() {
        // GIVEN
        val book = Ch15Book()
        book.name = "Some Book1"
        book.isbn = "12345.abcde"
        book.author = "John"
        book.price = 100
        book.stockQuantity = 20
        em.persist(book)
        em.flush()

        val order = Ch15Order()
        order.assignItem(book, 5)
        em.persist(order)
        em.flush()

        em.clear()

        // WHEN
        val orderItem = em.find(Ch15OrderItem::class.java, order.orderItems.first().id!!)
        val item = orderItem.item!!

        // item_javaClass=class com.example.springdb.study.jpabook.ch15_advanced_and_optimizing.models.Ch15Item$HibernateProxy
        log.info("item_javaClass={}", item.javaClass)

        assertFalse { item is Ch15Book }
        assertTrue { item is Ch15Item }
        assertFalse { item.javaClass == Ch15Book::class.java }
        assertThrows<ClassCastException> { item as Ch15Book }
    }

    @Test
    @DisplayName("Hibernate가 제공하는 기능을 사용하여 proxy로 부터 target(엔티티) 가져와서 상속관계 프록시 문제 해결")
    fun solution_unproxy() {
        // GIVEN
        val book = Ch15Book()
        book.name = "Some Book1"
        book.isbn = "12345.abcde"
        book.author = "John"
        book.price = 100
        book.stockQuantity = 20
        em.persist(book)
        em.flush()

        val order = Ch15Order()
        order.assignItem(book, 5)
        em.persist(order)
        em.flush()

        em.clear()

        // WHEN
        val orderItem = em.find(Ch15OrderItem::class.java, order.orderItems.first().id!!)
        val itemProxy = orderItem.item!!

        // item_javaClass=class com.example.springdb.study.jpabook.ch15_advanced_and_optimizing.models.Ch15Item$HibernateProxy
        log.info("itemProxy_javaClass={}", itemProxy.javaClass)

        // THEN
        // Hibernate 5.x 이하
        val target = (itemProxy as HibernateProxy).hibernateLazyInitializer.implementation
        assertTrue { target != itemProxy }
        assertTrue { target is Ch15Book }
        log.info("Using implementation: book_author={}", (target as Ch15Book).author)

        // Hibernate 6.X 이상
        val target2 = Hibernate.unproxy(itemProxy)
        assertTrue { target2 != itemProxy }
        assertTrue { target2 is Ch15Book }
        log.info("Using unproxy: book_author={}", (target2 as Ch15Book).author)

        /**
         * LOG:
         *      c.e.s.s.j.c.proxy.Ch15ProxyTest          : Using implementation: book_author=John
         *      c.e.s.s.j.c.proxy.Ch15ProxyTest          : Using unproxy: book_author=John
         * */
    }
}
