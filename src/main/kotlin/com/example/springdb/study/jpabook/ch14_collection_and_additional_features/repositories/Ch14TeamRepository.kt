package com.example.springdb.study.jpabook.ch14_collection_and_additional_features.repositories

import com.example.springdb.study.jpabook.ch14_collection_and_additional_features.models.Ch14Team
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface Ch14TeamRepository : JpaRepository<Ch14Team, Long> {

    @EntityGraph(value = "Ch14Team.withPlayers", type = EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT t FROM Ch14Team t WHERE t.id = :id")
    fun searchByTeamIdAndAllPlayers(@Param("id") id: Long): Ch14Team
}
