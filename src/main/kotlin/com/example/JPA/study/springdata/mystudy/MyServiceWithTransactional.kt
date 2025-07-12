package com.example.JPA.study.springdata.mystudy

import com.example.JPA.study.logger
import com.example.JPA.study.springdata.mystudy.models.Rock
import org.springframework.beans.factory.ObjectProvider
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class MyServiceWithTransactional(
    private val rockRepository: RockRepository,
    private val objectProvider: ObjectProvider<MyServiceWithTransactional>
) {
    private val log = logger()

    @Transactional
    fun saveRock(rockName: String, rockWeight: Int): Rock {
        val newRock = Rock(name = rockName, weightKgs = rockWeight)
        val savedRock = rockRepository.save(newRock)
        return savedRock
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun saveRocksWithRequiresNewPropagation(rockInfo: Map<String, Int>) {
        for((key, value) in rockInfo) {
            saveRockRequiresNew(key, value)
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun saveRocksWithRequiresNewPropagationObjectProvider(rockInfo: Map<String, Int>) {
        for((key, value) in rockInfo) {
            objectProvider.`object`.saveRockRequiresNew(key, value)
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun saveRockRequiresNew(rockName: String, rockWeight: Int): Rock {
        val newRock = Rock(name = rockName, weightKgs = rockWeight)
        val savedRock = rockRepository.save(newRock)
        return savedRock
    }

    @Transactional
    fun saveRocksTransactional1(rockInfo: Map<String, Int>) {
        saveRocksInnerNoTransactional(rockInfo)
    }

    @Transactional
    fun saveRocksTransactional2(rockInfo: Map<String, Int>) {
        saveRocksInnerTransactional(rockInfo)
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

    fun saveRocksInnerNoTransactional(rockInfo: Map<String, Int>) {
        val length = rockInfo.keys.size

        for((index, entry) in rockInfo.entries.withIndex()) {
            val rockName = entry.key
            val rockWeight = entry.value
            val newRock = Rock(name= rockName, weightKgs = rockWeight)
            rockRepository.save(newRock)
        }
    }
}