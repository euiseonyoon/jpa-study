package com.example.JPA.study.springdata.service

import com.example.JPA.study.logger
import com.example.JPA.study.springdata.mystudy.MyServiceWithTransactional
import org.hibernate.exception.ConstraintViolationException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class MyServiceInnerCallTest2 {

    private val log = logger()

    @Autowired
    lateinit var myServiceWithTransactional: MyServiceWithTransactional

    /**
     * 그러면 inner call 이면 트렌젝션이 무조건 보장이 안되나??
     *
     * */

    @Test
    @DisplayName("inner call 이어도 트렌젝션 보장 할 수 있다.")
    fun test1() {
        // weight > 0 인 constraint가 DB에 걸려있다.
        val rockInfo = mapOf<String, Int>(
            "rockName100" to 100,
            "rockName110" to 110,
            "rockName120" to -1,
        )
        assertThrows<ConstraintViolationException> {
            myServiceWithTransactional.saveRocksTransactional1(rockInfo)
        }
        // 결과: 트렌젝션 보장
        assertThrows<ConstraintViolationException> {
            myServiceWithTransactional.saveRocksTransactional2(rockInfo)
        }
        // 결과: 트렌젝션 보장

        /**
         * 결과:
         *      saveRocksTransactional() -> 내부호출 saveRocksInnerNoTransactional()
         *      saveRocksTransactional() -> 내부호출 saveRocksInnerTransactional()
         *      둘다 트렌젝션이 보장된다.
         *
         *  이유:
         *      트렌젝션 보장은 단순 inner call이라서 안되는것이 아니다.
         *
         *      saveRocksTransactional()이란 메소드는 @Transactional이 붙어있기때문에,
         *      saveRocksTransactional()이라는 메소드가 시작되면 전체적인 Transaction이 open된다. 그렇기 때문에 보장되는거다.
         *      트렌젝션open - save - save - save - 트렌젝션close 의 과정이 일어나는거다
         *
         *      하지만 saveRocks()이란 메소드에는 @Transactional이 붙어있지 않았다.
         *      saveRocks() 가 내부호출 하는 saveRocksInnerTransactional()에 @Transactional이 붙어있더라도,
         *
         *      트렌젝션open - save(rockName100) - 트렌젝션close
         *      트렌젝션open - save(rockName110) - 트렌젝션close
         *      트렌젝션open - save(rockName120) - 트렌젝션close
         *
         *      위 처럼 하나의 트렌젝션으로 묶인것이 아닌 여러개의 단발적 트렌젝션이 실행됬을 뿐이다.
         * */
    }
}
