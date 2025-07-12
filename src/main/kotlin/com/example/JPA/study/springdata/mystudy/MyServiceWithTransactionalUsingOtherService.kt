package com.example.JPA.study.springdata.mystudy

import com.example.JPA.study.logger
import com.example.JPA.study.springdata.jpa.projection.PostWithLikeCommentsRepository
import com.example.JPA.study.springdata.mystudy.models.Rock
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MyServiceWithTransactionalUsingOtherService(
    val myMiddleService: MyMiddleService
) {
    private val log = logger()

    @Transactional
    fun saveRock(): Rock {
        val savedRock = myMiddleService.saveRockInTheMiddle("rock1", 10)
        return savedRock
    }
}