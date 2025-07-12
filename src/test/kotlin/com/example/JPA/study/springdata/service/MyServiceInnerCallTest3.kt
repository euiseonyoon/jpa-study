package com.example.JPA.study.springdata.service

import com.example.JPA.study.logger
import com.example.JPA.study.springdata.mystudy.MyServiceWithTransactional
import com.example.JPA.study.springdata.mystudy.MyServiceWithTransactionalUsingOtherService
import org.hibernate.exception.ConstraintViolationException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataIntegrityViolationException

@SpringBootTest
class MyServiceInnerCallTest3 {

    private val log = logger()

    @Autowired
    lateinit var myServiceWithTransactional: MyServiceWithTransactional

    @Autowired
    lateinit var myServiceWithTransactionalUsingOtherService: MyServiceWithTransactionalUsingOtherService

    /**
     * 그러면 @Tranascational을 사용한다고 해도, Transaction을 구분 할 수 있다고 하던데(propagation)
     *
     *
     * */

    @Test
    @DisplayName("@Transactional propagation을 사용해도 inner call로 인해 트렌젝션 분리가 안되는 케이스")
    fun test1() {
        // weight > 0 인 constraint가 DB에 걸려있다.
        val rockInfo = mapOf<String, Int>(
            "rockName100" to 100,
            "rockName110" to 110,
            "rockName120" to -1,
        )
        myServiceWithTransactional.saveRocksWithRequiresNewPropagation(rockInfo)
        /**
         * 기대:
         *      myServiceWithTransactional.saveRocksWithRequiresNewPropagation()
         *      ->
         *      myServiceWithTransactional.saveRockRequiresNew() 3번 호출
         *
         *      myServiceWithTransactional.saveRockRequiresNew()에서 @Transactional(propagation = Propagation.REQUIRES_NEW) 옵션을 주었기 떄문에,
         *      rockName100, rockName110은 저장, rockName120은 저장이 안 될 것 같다.
         * 결과:
         *      모두 저장이 되지 않았다.
         *
         * 이유:
         *      1. 기존에 만들어진 트렌젝션에서 트렌젝션을 분리하려고 한다면 호출자가 아닌 호출받은 메소드가 끊어 내야한다.
         *      2. 이 과정중에 Transactional로 생성된 프록시의 advice를 거쳐야 한다.
         *      3. 하지만 inner 콜의 경우, 프록시를 거치지 않는다.
         *
         *      현상은 아래와 같다.
         *      saveRocksWithRequiresNewPropagation() --내부호출-> saveRockRequiresNew()를 3번 내부호출
         *
         *      4. 결국 saveRocksWithRequiresNewPropagation()가 생성한 1개의 트렌젝션안에서
         *              트렌젝션A open - save(in 트렌젝션A) - save(in 트렌젝션A) - save(in 트렌젝션A) - 트렌젝션A close
         *         가 된것이다.
         *
         * */
    }

    @Test
    @DisplayName("중간 서비스를 사용하여 분리, propagation이 잘 적용된 케이스")
    fun test2() {
        // weight > 0 인 constraint가 DB에 걸려있다.
        val rockInfo = mapOf<String, Int>(
            "rockName100" to 100,
            "rockName110" to 110,
            "rockName120" to -1,
        )
        assertThrows<DataIntegrityViolationException> {
            myServiceWithTransactionalUsingOtherService.saveRocksWithRequiresNewPropagation(rockInfo)
        }
        /**
         * 기대:
         *      1. 중간 proxy(MyMiddleService)를 거치기 때문에, 트렌젝션 분리가 됨.
         *      2. 1번의 이유로, rockName100, rockName110은 저장, rockName120은 저장이 안 될 것 같다.
         * 결과:
         *      기대와 동일.
         *
         * 설명:
         *      MyServiceWithTransactionalUsingOtherService(프록시).saveRocksWithRequiresNewPropagation()
         *      -->
         *      MyMiddleService(프록시).saveRockRequiresNew() * 3번 호출
         *      -->
         *      MyMiddleService(프록시)가 saveRockRequiresNew()의 REQUIRES_NEW 수행 (트렌젝션 분리)
         *      -->
         *      각각의 트렌젝션에서 save가 수행되었다.
         *
         *      트렌젝션 A open
         *      트렌젝션 A 중지
         *
         *      트렌젝션 가 open
         *      save
         *      트렌젝션 가 close
         *
         *      트렌젝션 나 open
         *      save
         *      트렌젝션 나 close
         *
         *      트렌젝션 다 open
         *      save
         *      트렌젝션 다 close
         *
         *      트렌젝션 A 복귀
         *      (donothing)
         *      트렌젝션 A close
         *
         * */
    }
}
