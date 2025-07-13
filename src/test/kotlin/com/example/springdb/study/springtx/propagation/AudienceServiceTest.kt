package com.example.springdb.study.springtx.propagation

import com.example.springdb.study.logger
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class AudienceServiceTest {

    private val log = logger()

    @Autowired
    lateinit var audienceService: AudienceService
    @Autowired
    lateinit var audienceRepository: AudienceRepository
    @Autowired
    lateinit var logRepository: LogRepository


    /**
     * audienceService       @Transactional: OFF
     * audienceRepository    @Transactional: ON
     * logRepository         @Transactional: ON
     * */
    @Test
    fun outerTxOff_success() {
        // GIVEN
        val username = "outerTxOff_success"

        // WHEN
        audienceService.joinV1(username)

        // THEN: 모든 데이터가 정상 저장된다.
        assertTrue { audienceRepository.find(username).isPresent }
        assertTrue { logRepository.find(username).isPresent }
    }

    /**
     * audienceService       @Transactional: OFF
     * audienceRepository    @Transactional: ON
     * logRepository         @Transactional: ON.  Exception 여기서 발생
     * */
    @Test
    fun outerTxOff_fail() {
        // GIVEN
        val username = "로그예외_outerTxOff_fail"

        // WHEN
        assertThrows<RuntimeException>{
            audienceService.joinV1(username)
        }

        // THEN: audience만 저장된다.
        assertTrue { audienceRepository.find(username).isPresent }
        assertTrue { logRepository.find(username).isEmpty }
    }

    /**
     * audienceService       @Transactional: ON
     * audienceRepository    @Transactional: ON
     * logRepository         @Transactional: ON
     * */
    @Test
    fun outerTxOn_success() {
        // GIVEN
        val username = "outerTxOn_success"

        // WHEN
        audienceService.joinV1(username)

        // THEN: 모든 데이터가 정상 저장된다.
        assertTrue { audienceRepository.find(username).isPresent }
        assertTrue { logRepository.find(username).isPresent }
    }

    /**
     * audienceService       @Transactional: ON
     * audienceRepository    @Transactional: ON
     * logRepository         @Transactional: ON.  Exception 여기서 발생
     * */
    @Test
    fun outerTxOn_fail() {
        // GIVEN
        val username = "로그예외_outerTxOn_fail"

        // WHEN
        assertThrows<RuntimeException>{
            audienceService.joinV1(username)
        }

        // THEN
        assertTrue { audienceRepository.find(username).isEmpty }
        assertTrue { logRepository.find(username).isEmpty }
    }

}