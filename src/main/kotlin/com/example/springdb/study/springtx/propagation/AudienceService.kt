package com.example.springdb.study.springtx.propagation

import com.example.springdb.study.logger
import org.springframework.stereotype.Service

@Service
class AudienceService(
    private val audienceRepository: AudienceRepository,
    private val logRepository: LogRepository
) {
    private val log = logger()

    // 각각 다른 transaction을 알아보는 예제
    fun joinV1(username: String) {
        val audience = Audience(username)
        val logMessage = Log(username)

        log.info("=== audienceRepository 호출 시작 ===")
        audienceRepository.save(audience)
        log.info("=== audienceRepository 호출 종료 ===")

        log.info("=== logRepository 호출 시작 ===")
        logRepository.save(logMessage)
        log.info("=== logRepository 호출 시작 ===")
    }

    // 각각 다른 transaction을 알아보는 예제
    fun joinV2(username: String) {
        val audience = Audience(username)
        val logMessage = Log(username)

        log.info("=== audienceRepository 호출 시작 ===")
        audienceRepository.save(audience)
        log.info("=== memberRepository 호출 종료 ===")

        log.info("=== audienceRepository 호출 시작 ===")
        try {
            logRepository.save(logMessage)
        } catch (e: Exception) {
            log.info("log 저장에 실패했어요. logMessage={}", logMessage.message)
            log.info("정상 흐름 반환")
        }
        log.info("=== logRepository 호출 종료 ===")
    }
}
