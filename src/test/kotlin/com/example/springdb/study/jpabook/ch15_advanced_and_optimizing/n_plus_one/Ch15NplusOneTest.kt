package com.example.springdb.study.jpabook.ch15_advanced_and_optimizing.n_plus_one

import com.example.springdb.study.jpabook.ch15_advanced_and_optimizing.models.Ch15Member
import com.example.springdb.study.jpabook.ch15_advanced_and_optimizing.models.Ch15Order
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class Ch15NplusOneTest {
    @Autowired
    @PersistenceContext
    lateinit var em: EntityManager

    var members: List<Ch15Member> = listOf()

    @BeforeEach
    fun init() {
        members =
            listOf("Michael", "John", "Jonathan", "Tony", "Sam").map {
                addMemer(it)
            }

        em.flush()
    }

    private fun addMemer(name: String): Ch15Member {
        val member = Ch15Member()
        member.name = name
        em.persist(member)
        return member
    }

    private fun makeOrder(member: Ch15Member): Ch15Order {
        val order = Ch15Order()
        order.assignMember(member)
        em.persist(order)
        em.flush()
        return order
    }

    @Test
    fun fetch_type_eager() {
        // GIVEN
        val randomMember = members.shuffled().first()
        for (i in 0..4) {
            makeOrder(randomMember)
        }
        em.clear()
        val result1 = em.find(Ch15Member::class.java, randomMember.id)
        /**
         * 결과:
         *      Member 조회시 Order를 FetchType.EAGER로 설정하면 em.find()를 통해 조회시, join해서 한번에 잘 가져온다
         *
         * LOG:
         *      select
         *         cm1_0.id,
         *         cm1_0.name,
         *         o1_0.member_id,
         *         o1_0.id,
         *         o1_0.order_date,
         *         o1_0.status
         *     from
         *         ch15member cm1_0
         *     left join
         *         ch15order o1_0
         *             on cm1_0.id=o1_0.member_id
         *     where
         *         cm1_0.id=?
         * */

        em.clear()
        val result2 = em.createQuery("SELECT m FROM Ch15Member m").resultList

        /**
         * 결과:
         *      1. Ch15Member 테이블에서 맴버를 가져오고 (1번)
         *      2. Ch15Order 테이블에서 그 맴버의 Order를 또 가져온다 (5번)
         *
         * LOG:
         *
         *   select
         *         cm1_0.id,
         *         cm1_0.name
         *    from
         *         ch15member cm1_0
         *
         *    아래 쿼리는 5번 실행됨
         *    select
         *         o1_0.member_id,
         *         o1_0.id,
         *         o1_0.order_date,
         *         o1_0.status
         *     from
         *         ch15order o1_0
         *     where
         *         o1_0.member_id=?
         *
         * */

        /**
         * FetchType.EAGER 도 JPQL 쿼리를 직접 만들어서 사용하는 경우 N+1을 해결 하지 못한다.
         * FetchType.LAZY도 마찬가지.
         *
         * FetchType.LAZY도 `SELECT m FROM Ch15Member m`만 사용할 경우 상관없지만.
         * member.orders.size() 처럼 실행하면  Ch15Order에서 member_id = 멤버 id인 쿼리를 1번 더 한다.
         *
         * 만약 모든 유저마다 member.orders.size()를 호출하면
         * 모든 멤버를 가져오는 쿼리 1개 + 맴버 수마다 order를 가져오는 쿼리 N개가 발생한다.
         * */
    }

    @Test
    fun solution() {
        // 1. FETCH JOIN
        val fetchJoinResult = em.createQuery("SELECT m FROM Ch15Member m JOIN FETCH m.orders").resultList

        // 2. @BatchSize 사용

        // 3. @Fetch(FetchMode.SUBSELECT) 사용
        /**
         * 위 옵션 사용시, 연관된 데이터 조회시 서브쿼리를 사용하여 해결한다.
         * e.g.
         *
         *      SELECT O FROM ch15Order O
         *      WHERE O.member_id IN (
         *          SELECT M FROM ch15Member WHERE id > ?
         *      )
         * */
    }
}
