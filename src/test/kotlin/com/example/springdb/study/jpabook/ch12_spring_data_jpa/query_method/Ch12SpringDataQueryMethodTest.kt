package com.example.springdb.study.jpabook.ch12_spring_data_jpa.query_method

import com.example.springdb.study.jpabook.ch12_spring_data_jpa.models.Ch12Member
import com.example.springdb.study.jpabook.ch12_spring_data_jpa.repositories.Ch12MemberRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import kotlin.test.assertEquals

@DataJpaTest
class Ch12SpringDataQueryMethodTest {

    @Autowired
    lateinit var memberRepository: Ch12MemberRepository

    @Autowired
    @PersistenceContext
    lateinit var em: EntityManager

    var members: List<Ch12Member> = listOf()

    private fun addMemer(name: String): Ch12Member {
        val member = Ch12Member()
        member.name = name

        return memberRepository.save(member)
    }

    @BeforeEach
    fun initTest() {
        // GIVEN
        members = listOf("Michael", "John", "Jonathan", "Tony", "Sam").map {
            addMemer(it)
        }

        em.flush()
        em.clear()
    }

    @Test
    fun query_by_method_names() {
        val result = memberRepository.findByName("Tony")
        assertEquals(1, result.size)
        assertEquals("Tony", result.first().name)
    }

    @Test
    fun named_query_annotation() {
        // 1. JPA를 직접 호출하여 NamedQuery 사용
        // WHEN
        val targetName = "Tony"
        val result = em.createNamedQuery("Ch12Member.searchByUsername")
            .setParameter("username", targetName)
            .resultList // (JPA를 직접사용) singleResult를 사용시, 결과가 없으면 javax.persistence.NoResultException이 발생한다.
        // THEN
        assertEquals(1, result.size)
        assertEquals(targetName, (result.first() as Ch12Member).name)

        // 2. 스프링 데이터 JPA를 통하여 NamedQuery 사용
        // WHEN
        val result2 = memberRepository.searchByUsername(targetName)
        // THEN
        assertEquals(1, result2.size)
        assertEquals(targetName, result2.first().name)
    }

    @Test
    fun query_annotation() {
        fun assertTony(result: List<Ch12Member>) {
            assertEquals(1, result.size)
            assertEquals("Tony", result.first().name)
        }

        // JPQL 사용하는 @Query
        memberRepository.searchByUsernameJpql("Tony").let {
            assertTony(it)
        }

        // @Query(nativeQuery = true) 사용
        memberRepository.searchByUsernameQueryNativeTrue("Tony").let {
            assertTony(it)
        }

        // @NativeQuery 어노테이션을 사용
        memberRepository.searchByUsernameNativeQueryAnnotation("Tony").let {
            assertTony(it)
        }
    }
}
