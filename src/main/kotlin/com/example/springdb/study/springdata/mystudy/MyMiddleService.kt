package com.example.springdb.study.springdata.mystudy

import com.example.springdb.study.springdata.mystudy.models.Rock
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun saveRockRequiresNew(rockName: String, rockWeight: Int): Rock {
        val newRock = Rock(name = rockName, weightKgs = rockWeight)
        val savedRock = rockRepository.save(newRock)
        return savedRock
    }
}
