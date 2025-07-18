package com.example.springdb.study.jpabook.ch14_collection_and_additional_features.repositories

import com.example.springdb.study.jpabook.ch14_collection_and_additional_features.converter.Ch14PlayerDto
import com.example.springdb.study.jpabook.ch14_collection_and_additional_features.models.Ch14Player
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.NativeQuery

interface Ch14PlayerRepository : JpaRepository<Ch14Player, Long> {

    @NativeQuery("SELECT * FROM ch14player", sqlResultSetMapping = "Ch14PlayerNativeQueryDto")
    fun searchAllPlayers(): List<Ch14PlayerDto>
}
