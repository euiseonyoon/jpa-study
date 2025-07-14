package com.example.springdb.study.springtx.propagation

import com.example.springdb.study.logger
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.UnexpectedRollbackException

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

    /**
     * 가정: Transaction(propagation = REQUIRED)
     *
     * 상황:
     *   1. log를 저장하던중 RuntimeException 발생
     *   2. 그러면 해당 Exception을 catch해서 핸들링하면 되지 않을까?
     *
     * 결과:
     *      UnexpectedRollbackException 발생 (서비스단에서 커밋하려고 할떄)
     *      audience, log 둘 다 롤백
     *
     * 이유:
     *      1. 서비스단에서 새 물리 트렌젝션 획득
     *      2. audience가 위 물리 트렌젝션에 참여하여 저장
     *      3. log가 위 물리 트렌젝션에 참여하여 저장중 에러 발생 -> 해당 물리 트렌젝션에 `rollbackOnly`로 마킹해버림
     *      4. 서비스단에서 물리 트렌젝션을 commit 하려고함 -> 해당 물리 트렌젝션은 `rollbackOnly`로 인해 커밋되지 않음
     *
     * 해결방법:
     *      1. logRepository의 save() @Transactional의 propagation 레벨을 REQUIRES_NEW
     *      2. audience를 저장하는 과정과 log를 저장하는 과정을 따로 하는것
     *      (서비스단에서 한 method에 묶지 말고, audience 저장 method, log 저장 method를 따로두고, 각각 @Transactional(Required) 적용)
     *
     * audienceService       @Transactional: ON
     * audienceRepository    @Transactional: ON
     * logRepository         @Transactional: ON.  Exception 여기서 발생
     * */
    @Test
    fun recoverException_fail() {
        // GIVEN
        val username = "로그예외_recoverException_fail"

        // WHEN
        assertThrows<UnexpectedRollbackException>{
            audienceService.joinV2(username)
        }

        // THEN
        assertTrue { audienceRepository.find(username).isEmpty }
        assertTrue { logRepository.find(username).isEmpty }
    }

}