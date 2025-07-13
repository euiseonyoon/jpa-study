package com.example.springdb.study.springdata.mystudy

import com.example.springdb.study.springdata.mystudy.models.Rock
import org.springframework.data.jpa.repository.JpaRepository

interface RockRepository: JpaRepository<Rock, Long> {
}