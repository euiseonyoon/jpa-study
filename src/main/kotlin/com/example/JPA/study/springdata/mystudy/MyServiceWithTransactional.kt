package com.example.JPA.study.springdata.mystudy

import com.example.JPA.study.logger
import com.example.JPA.study.springdata.jpa.projection.PostWithLikeCommentsRepository
import com.example.JPA.study.springdata.mystudy.models.Rock
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MyServiceWithTransactional(
    private val rockRepository: RockRepository
) {
    private val log = logger()

    @Transactional
    fun saveRock(rockName: String, rockWeight: Int): Rock {
        val newRock = Rock(name = rockName, weightKgs = rockWeight)
        val savedRock = rockRepository.save(newRock)
        return savedRock
    }
}