package com.example.springdb.study.jpabook.ch15_advanced_and_optimizing.proxy

import com.example.springdb.study.jpabook.ch15_advanced_and_optimizing.models.Ch15Member
import com.example.springdb.study.logger
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
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
}
