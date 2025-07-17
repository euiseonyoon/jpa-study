package com.example.springdb.study.jpabook.ch12_spring_data_jpa.repositories

import com.example.springdb.study.jpabook.ch12_spring_data_jpa.models.Ch12Item
import com.example.springdb.study.jpabook.ch12_spring_data_jpa.projections.Ch12ItemNameOnlyDto
import com.example.springdb.study.jpabook.ch12_spring_data_jpa.projections.Ch12ItemNativeDto
import com.example.springdb.study.jpabook.ch12_spring_data_jpa.projections.Ch12ItemNativeInterfaceResult
import com.example.springdb.study.jpabook.ch12_spring_data_jpa.projections.Ch12ItemStockQuantityOnly
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.NativeQuery
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface Ch12ItemRepository : JpaRepository<Ch12Item, Long> {

    // @Query, @NativeQuery가 아닌 Derived queries -> Interface 예시
    // https://docs.spring.io/spring-data/jpa/reference/repositories/projections.html#_derived_queries
    fun findByName(name: String): List<Ch12ItemStockQuantityOnly>

    // @Query, @NativeQuery가 아닌 Derived queries -> Class Dto 예시
    fun findByPrice(price: Int): List<Ch12ItemNameOnlyDto>

    // interface 타입으로 projction 할 경우
    @Query("SELECT i FROM Ch12Item i WHERE i.name = :name")
    fun searchByNameToInterface(@Param("name") name: String): List<Ch12ItemStockQuantityOnly>

    // Class Dto로 projection 할 경우
    /**
     * @Query("SELECT i FROM Ch12Item i WHERE i.name = :name")
     * 처럼 new com.example.springdb.study.jpabook.ch12_spring_data_jpa.projections.Ch12ItemNameOnlyDto 없이 사용해도 작동했다.
     * 히지만 이건 docs에 나온 표준 방식은 아닌것 같다.
     *
     * `SELECT i FROM Ch12Item i WHERE i.name = :name` 이렇게 간단 하게 사용하면 이 쿼리는 내부적으로
     * `SELECT
     *      new com.example.springdb.study.jpabook.ch12_spring_data_jpa.projections.Ch12ItemNameOnlyDto(i.name)
     *  FROM Ch12Item i
     *  WHERE i.name = :name` 처럼 rewrite 된다고 한다.
     *
     *  가능하면 표준을 따르도록 하자
     * */
    @Query("""
       SELECT 
       new com.example.springdb.study.jpabook.ch12_spring_data_jpa.projections.Ch12ItemNameOnlyDto(i.name)
       FROM Ch12Item i 
       WHERE i.name = :name 
    """)
    fun searchByNameToDto(@Param("name") name: String): List<Ch12ItemNameOnlyDto>


    @NativeQuery(
        value = "SELECT * FROM ch12item WHERE ch12item.id = :id",
        sqlResultSetMapping = "Ch12ItemNativeQueryDto"
    )
    fun searchByIdUsingNativeQueryToDto(@Param("id") id: Long): Ch12ItemNativeDto

    @NativeQuery(
        value = "SELECT * FROM ch12item WHERE ch12item.id = :id",
    )
    fun searchByIdUsingNativeQueryToInterface(@Param("id") id: Long): Ch12ItemNativeInterfaceResult
}