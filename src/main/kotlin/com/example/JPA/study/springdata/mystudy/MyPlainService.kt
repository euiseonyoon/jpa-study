package com.example.JPA.study.springdata.mystudy

import com.example.JPA.study.logger
import com.example.JPA.study.springdata.jpa.projection.PostWithLikeCommentsRepository
import org.springframework.stereotype.Service

@Service
class MyPlainService(
    private val rockRepository: RockRepository
) {
    private val log = logger()

    fun simpleGet(id: Long) {
        log.info("-- myService simpleGet")
        val result = rockRepository.findById(id).orElse(null)
    }
}