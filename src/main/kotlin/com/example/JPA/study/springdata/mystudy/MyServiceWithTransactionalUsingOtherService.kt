package com.example.JPA.study.springdata.mystudy

import com.example.JPA.study.logger
import com.example.JPA.study.springdata.mystudy.models.Rock
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MyServiceWithTransactionalUsingOtherService(
    val myMiddleService: MyMiddleService
) {
    private val log = logger()

    @Transactional
    fun saveRock(rockName: String, rockWeight: Int): Rock {
        val savedRock = myMiddleService.saveRockInTheMiddle(rockName, rockWeight)
        return savedRock
    }

    @Transactional
    fun saveRocks(rockInfo: Map<String, Int>) {
        val length = rockInfo.keys.size

        for((index, entry) in rockInfo.entries.withIndex()) {
            val rockName = entry.key
            val rockWeight = entry.value
            myMiddleService.saveRockInTheMiddle(rockName, rockWeight)
        }
    }
}