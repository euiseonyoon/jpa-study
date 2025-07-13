package com.example.springdb.study.springdata.mystudy

import com.example.springdb.study.logger
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