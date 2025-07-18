package com.example.springdb.study.springtx.exception.code

import com.example.springdb.study.logger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RollbackService {
    private val log = logger()

    // 런타임 예외 발생: 롤백
    @Transactional()
    fun runtimeException() {
        log.info("call runtimeException")
        throw RuntimeException()
    }

    // 체크 예외 발생: 커밋
    @Transactional
    fun checkedException() {
        log.info("call checkedException")
        throw MyException()
    }

    // 체크 예외 발생: 롤백 (rollbackFor)
    @Transactional(rollbackFor = [MyException::class])
    fun rollbackFor() {
        log.info("call rollbackFor")
        throw MyException()
    }
}
