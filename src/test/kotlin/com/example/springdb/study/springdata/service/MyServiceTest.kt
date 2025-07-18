package com.example.springdb.study.springdata.service

import com.example.springdb.study.logger
import com.example.springdb.study.springdata.mystudy.MyPlainService
import com.example.springdb.study.springdata.mystudy.MyServiceWithTransactional
import com.example.springdb.study.springdata.mystudy.MyServiceWithTransactionalUsingOtherService
import com.example.springdb.study.springdata.mystudy.RockRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.aop.support.AopUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.env.Environment

// @SpringBootTest(properties = ["spring.aop.proxy-target-class=true"])
@SpringBootTest
class MyServiceTest {

    private val log = logger()

    @Autowired
    lateinit var environment: Environment

    @Autowired
    lateinit var myPlainService: MyPlainService

    @Autowired
    lateinit var myServiceWithTransactional: MyServiceWithTransactional

    @Autowired
    lateinit var myServiceWithTransactionalUsingOtherService: MyServiceWithTransactionalUsingOtherService

    @Autowired
    lateinit var rockRepository: RockRepository

    @Test
    @DisplayName(
        """
        1. @Transactional이 붙지 않은 @Service가 과연 proxy인지 확인
        2. JpaRepository를 이어받은 RockRepository(interface)는 proxy 인지 확인
        3. 2번에서 RockRepository가 proxy라면, 어떤 proxy인지 확인 (JDK 동적 proxy || CGLIB 프록시)
    """
    )
    fun test1() {
        val isPlainServiceProxy = AopUtils.isAopProxy(myPlainService)
        val isRepoProxy = AopUtils.isAopProxy(rockRepository)
        val isRepoJdkProxy = AopUtils.isJdkDynamicProxy(rockRepository)
        val isRepoCglibProxy = AopUtils.isCglibProxy(rockRepository)

        log.info("-- isServiceProxy={}", isPlainServiceProxy) // false
        log.info("-- isRepoProxy={}", isRepoProxy) // true
        log.info("-- isRepoJdkProxy={}", isRepoJdkProxy) // true
        log.info("-- isRepoCglibProxy={}", isRepoCglibProxy) // false.

        /**
         * NOTE(결론):
         *  1. @Transactional이 붙지 않은 @Service는 프록시가 아니었다.
         *  2. RockRepository(interface) 처럼 JpaRepository를 이어받은 경우
         *     spring.aop.proxy-target-class 의 값에 상관없이 JDK 동적 프록시였다.
         * */
    }

    @Test
    @DisplayName(
        """
        1. @Transactional이 붙은 @Service가 과연 proxy인지 확인
        2. 만약 1번에서 proxy로 확인된다면, 어떤 프로시인지 확인
    """
    )
    fun test2() {
        val isTransactionalServiceProxy = AopUtils.isAopProxy(myServiceWithTransactional)
        val isJdkProxy = AopUtils.isJdkDynamicProxy(myServiceWithTransactional)
        val isCglibProxy = AopUtils.isCglibProxy(myServiceWithTransactional)
        log.info("-- isTransactionalServiceProxy={}", isTransactionalServiceProxy) // true
        log.info("-- isJdkProxy={}", isJdkProxy) // false
        log.info("-- isCglibProxy={}", isCglibProxy) // true

        /**
         * NOTE(결론):
         *  1. @Transactional이 붙은 @Service는 프록시화 된다.
         *  2. 현재 코드에서는 CGLIB 프록시 화 되었다.
         *
         *  추측:
         *    만약 MyServiceWithTransactional 이 어떤 interface를 구현하고 있고,
         *    proxyTargetClass=false 라면 jdk 프록시로 생성될수도 있을것 같다.
         * */
    }

    @Test
    @DisplayName(
        """
        @Service가 @Transactional로 인해 proxy화 되어도, 
        프록시 내부 호출 문제로 인해, 외부 서비스로 분리를 해야하는 경우가 있다.
        이러한 과정중 어디어디가 proxy인지 확인해 본다.
        
        request flow:
            @Service with @Transactional -> 중간 다른 @Service without @Transactional -> RockRepository
    """
    )
    fun test3() {
        val middleService = myServiceWithTransactionalUsingOtherService.myMiddleService

        val isMyServiceProxy = AopUtils.isAopProxy(myServiceWithTransactionalUsingOtherService)
        val isMiddleServiceProxy = AopUtils.isAopProxy(middleService)
        val isRockRepoProxy = AopUtils.isAopProxy(rockRepository)

        log.info("-- isMyServiceProxy={}", isMyServiceProxy)
        log.info("-- isMiddleServiceProxy={}", isMiddleServiceProxy)
        log.info("-- isRockRepoProxy={}", isRockRepoProxy)

        /***
         * NOTE(결론):
         *      1. @Service with @Transactional : 프록시가 맞음
         *      2. @Service without @Transactional: 프록시가 아님
         *      3. RockRepository(interface JpaRepository): 프록시가 맞음
         *
         * 궁금중:
         *      1. 그러면 이러한 경우에 Transaction이 보장이 되는가?
         *      2. 된다면 어떠한 원리/이유로 ? (중간 @Service without @Transactional도 proxy가 되어야 transaction 이 보장되지 않을까?)
         */
    }

    @Test
    @DisplayName("간단한 새로운 Rock을 save 하는 테스트")
    fun test4() {
        val savedRock = myServiceWithTransactionalUsingOtherService.saveRock("rockName1", 10)
        // 결과 : 실제 DB에 잘 저장됨

        assertThrows<Exception> {
            myServiceWithTransactionalUsingOtherService.saveRock("rockName1", -1)
        }
    }

    @Test
    @DisplayName(
        """
        여러 Rock을 save 한다. 하지만 의도적으로 마지막 rock을 save할때 무게를 음수처리하여 DB 오류를 발생시킨다.
        목적: MiddleService는 proxy가 아닌데, 과연 트렌젝션 보장이 되는가? 
    """
    )
    fun test4_1() {
        val rockInfo = mapOf<String, Int>(
            "rockName10" to 10,
            "rockName11" to 11,
            "rockName12" to -1
        )
        myServiceWithTransactionalUsingOtherService.saveRocks(rockInfo)

        /**
         * 결론: rock들이 하나도 저장안됨, 트렌젝션 보장
         * 그러면 왜 중간 MiddleeService는 프록시도 아닌데 트렌젝션이 보장이 될까?
         * 이유:
         *      1. myServiceWithTransactionalUsingOtherService.saveRocks()는 @Transactional이라는 어노테이션이 붙어있음
         *      2. 해당 어노테이션으로 인해. MyServiceWithTransactionalUsingOtherService의 saveRocks()라는 메소드가 실행될때,
         *          트렌젝션 보장을 위한 advice들이 적용됨
         *      3. 그래서 proxy가 아닌 MyMiddleService 에서 에러가 나도 advice로 전파가 되고 -> 이를 통해 롤백/커밋을 함.
         *
         * */
    }
}
