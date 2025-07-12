package com.example.JPA.study.springdata.mystudy

import com.example.JPA.study.springdata.mystudy.models.Rock
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class MyMiddleService(
    private val rockRepository: RockRepository
) {
    fun saveRockInTheMiddle(name: String, weightKgs: Int): Rock {
        val newRock = Rock(name = name, weightKgs = weightKgs)
        val savedRock = rockRepository.save(newRock)
        return savedRock
    }

    @Transactional
    fun saveRockInTheMiddleTransactional(name: String, weightKgs: Int): Rock {
        val newRock = Rock(name = name, weightKgs = weightKgs)
        val savedRock = rockRepository.save(newRock)
        return savedRock
    }
}