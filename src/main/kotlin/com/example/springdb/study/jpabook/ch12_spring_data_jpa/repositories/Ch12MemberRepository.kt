package com.example.springdb.study.jpabook.ch12_spring_data_jpa.repositories

import com.example.springdb.study.jpabook.ch12_spring_data_jpa.models.Ch12Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.NativeQuery
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface Ch12MemberRepository : JpaRepository<Ch12Member, Long> {

    fun findByName(name: String): List<Ch12Member>

    // "Ch12Member.searchByUsername" named query와 아래의 함수명이 동일.
    fun searchByUsername(@Param("username") username: String): List<Ch12Member>

    // JPQL을 사용할 때에는 파라미터의 인덱스가 1부터 시작
    @Query("SELECT m from Ch12Member m WHERE m.name = ?1")
    fun searchByUsernameJpql(name: String): List<Ch12Member>

    // Native Query를 사용할 때에는 파라미터의 인덱스가 1부터 시작 (책에는 0부터 시작이라고 했으나 docs를 확인해보면 1부터)
    // 문서 : https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html#jpa.query-methods.at-query
    //       (Spring Data JPA 3.5.1)
    @Query("SELECT * FROM ch12member WHERE name = ?1", nativeQuery = true)
    fun searchByUsernameQueryNativeTrue(name: String): List<Ch12Member>

    // 위의 문서를 둘러보니, @NativeQuery 라는 것을 발견했다. 여기서는 기존 JPA에서 사용하던 @SqlResultSetMapping을 사용하던걸
    // sqlResultSetMapping 옵션을 주어서 해결 할 수 있게 했다. 이건 Projection 관련해서 추가로 봐야겠다.
    @NativeQuery("SELECT * FROM ch12member WHERE name = ?1")
    fun searchByUsernameNativeQueryAnnotation(name: String): List<Ch12Member>
}
