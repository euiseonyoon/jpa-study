package com.example.springdb.study.jpabook.ch14_collection_and_additional_features.repositories

import com.example.springdb.study.jpabook.ch14_collection_and_additional_features.models.Ch14Order
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface Ch14OrderRepository : JpaRepository<Ch14Order, Long> {

    /**
     * EntityGraph.EntityGraphType.FETCH :
     *      엔티티 그래프에서 attributeNodes로 지정된 것들은 EAGER로.
     *      지정되지 않은 다른 것들은 LAZY로 가져옴
     *
     * EntityGraph.EntityGraphType.LOAD :
     *      엔티티 그래프에서 attributeNodes로 지정된 것들은 EAGER로,
     *      지정되지 않은 다른 것들은 명시된 FetchType을 따르거나, default FetchType을 따름
     * */
    @EntityGraph(value = "Ch14Order.withMember", type = EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT o FROM Ch14Order o WHERE o.id = :id")
    fun searchByIdWithMember(@Param("id") id: Long): Ch14Order

    @EntityGraph(value = "Ch14Order.withAll", type = EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT o FROM Ch14Order o WHERE o.id = :id")
    fun searchByIdWithAll(@Param("id") id: Long): Ch14Order

    /**
     * 여기서는 위에서 보여지둣, 이미 만들어진 EntityGraph를 사용하기 때문에, 동적으로 EntityGraph를 만들어서 사용할 수 없다.
     * 정 원한다면 Ch14OrderExtension(interface) + Ch14OrderExtensionImpl(구현체)를 만들고,
     * 해당 fragment 에서 엔티티 메니저를 사용해서 동적으로 EntityGraph를 만들어서 사용 할 수는 있을것 같다.
     * */
}
