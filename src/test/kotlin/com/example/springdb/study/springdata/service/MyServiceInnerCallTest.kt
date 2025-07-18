package com.example.springdb.study.springdata.service

import com.example.springdb.study.logger
import com.example.springdb.study.springdata.mystudy.MyServiceWithTransactional
import com.example.springdb.study.springdata.mystudy.MyServiceWithTransactionalUsingOtherService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.env.Environment
import kotlin.collections.iterator

@SpringBootTest
class MyServiceInnerCallTest {

    private val log = logger()

    @Autowired
    lateinit var environment: Environment

    @Autowired
    lateinit var myServiceWithTransactional: MyServiceWithTransactional

    @Autowired
    lateinit var myServiceWithTransactionalUsingOtherService: MyServiceWithTransactionalUsingOtherService

    @Test
    @DisplayName("전형적으로 발생하는 inner() call로 인한 트렌젝션 미보장 케이스")
    fun test1() {
        // weight > 0 인 constraint가 DB에 걸려있다.
        val rockInfo = mapOf<String, Int>(
            "rockName100" to 100,
            "rockName110" to 110,
            "rockName120" to -1
        )
        myServiceWithTransactional.saveRocks(rockInfo)
        /**
         * 결과:
         *      rockName100, rockName110은 저장되었고, rockName120은 저장 안됨
         *      트렌젹션이 보장안됨!!
         * */
    }

    @Test
    @DisplayName("test1의 해결로 외부로 db액션을 외부로 빼라는 해결책이 많다. 실험해 본다.")
    fun test2() {
        // weight > 0 인 constraint가 DB에 걸려있다.
        val rockInfo = mapOf<String, Int>(
            "rockName100" to 100,
            "rockName110" to 110,
            "rockName120" to -1
        )
        for ((name, weight) in rockInfo) {
            myServiceWithTransactionalUsingOtherService.saveRock(name, weight)
        }
        // 결과: 트렌젝션 미보장.. 이렇게 하면 rockName100, rockName110 저장, rockName120은 없음
        // 이유: saveRock은 각각의 save를 서로 다른 트렌젝션으로 수행하니까 당연히 안된다. (트렌젝션 시작-끝) * 3번 이었을 뿐이다.

        myServiceWithTransactionalUsingOtherService.saveRocks(rockInfo)
        // 결과: 트렌젝션 보장. 이렇게 하면 모두 미저장
        // 이유: saveRocks는 트렌젝션open - save - save -save - 트렌젝션close 니까.
    }
}
