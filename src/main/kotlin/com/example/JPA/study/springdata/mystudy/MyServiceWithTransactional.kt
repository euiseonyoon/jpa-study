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

    fun saveRocks(rockInfo: Map<String, Int>) {
        saveRocksInnerTransactional(rockInfo)
    }

    @Transactional
    fun saveRocksInnerTransactional(rockInfo: Map<String, Int>) {
        val length = rockInfo.keys.size

        for((index, entry) in rockInfo.entries.withIndex()) {
            val rockName = entry.key
            val rockWeight = entry.value
            val newRock = Rock(name= rockName, weightKgs = rockWeight)
            rockRepository.save(newRock)
        }
    }
}