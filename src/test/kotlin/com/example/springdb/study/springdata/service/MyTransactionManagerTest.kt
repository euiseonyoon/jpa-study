package com.example.springdb.study.springdata.service

import com.example.springdb.study.logger
import com.example.springdb.study.springdata.mystudy.MyTransactionManagerService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class MyTransactionManagerTest {

    private val log = logger()

    @Autowired
    lateinit var myTransactionManagerService: MyTransactionManagerService

    @Test
    @DisplayName(
        """
        @Transactional이라는 어노테이션은 단순 DB 트렌젝션을 보장하기 위함은 아니다.
    """
    )
    fun test1() {
        myTransactionManagerService.customTxManager()

        /**
         * Log:
         *      c.e.J.s.s.mystudy.MyTransactionManager   : [MyTransactionManager] getTransaction
         *      c.e.J.s.s.mystudy.MyTransactionManager   : [MyTransactionManager] commit
         * */

        /**
         * NOTE:
         *      @Transactional은 단순 DB 트렌젝션을 보장하기 위한 어노테이션이 아니다.
         *      해당 어노테이션은 Proxy를 만들어주고 joinPoint를 설정해서, 트렌젝션(어떤것을 보내는)을 보장하기 위함이다.
         *
         *      DB의 관점에서 본다면 데이터의 CRUD가 잘 되는것이 트렌젝션이고
         *      message 전달의 관점에서 본다면 메시지의 전달이 잘 되는것이 트렌젝션이고
         *      (e.g. Spring AMQP와 같이 메시지를 전달.수신 하는데 사용되는 라이브러리도 @Transaction을 사용한다.)
         *      트렌젝션의 의미는 컨텍스트마다 다를 수 있다.
         *
         *      @Transactional 에는 transactionManager 라는 옵션을 줄 수 있다.
         *      이 메니저는 어노테이션으로 생성된 joinPoint에 어떤 advice를 심어줄 메니저이다.
         *
         *      JPA(Hibernate)는 JpaTransactionManager가 영속성 관리 + DB 트렌젝션 관리를 하는 advice를 심는다.
         *      (https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/orm/jpa/JpaTransactionManager.html)
         *
         *      MyBatis는 스프링의 DataSourceTransactionManager가 DB 트렌젝션을 관리하는 advice를 심는다.
         *      (https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/datasource/DataSourceTransactionManager.html)
         *
         *      Spring AMQP는 RabbitTransactionManager가 메세지 트렌젝션을 관리하는 advice를 심는다.
         *      (https://docs.spring.io/spring-amqp/api/org/springframework/amqp/rabbit/transaction/RabbitTransactionManager.html)
         *
         *      위의 docs 링크에서 확인해보면 모두 org.springframework.transaction.PlatformTransactionManager 의 구현체임을 확인 할 수 있다.
         *
         *      @Transactional 어노테이션 1개에는 1개의 트렌젝션 메니저만 등록할 수 있다.
         *
         *      결론:
         *          @Transactional 은 단순 DB 트렌젝션의 보장을 위한 어노테이션이 아니다.
         *          해당 어노테이션은 어떠한 advice가 적용 될 위치(joinPoint)를 지정하는 것이며,
         *          해당 위치에 어떠한 advice를 심을지는 transactionManager가 관리하는것이다.
         *          transactionManager는 컨텍스트에 따라 DB 트렌젝션을 보장하는 메니저인지
         *          메시지 트렌젝션을 보장하는 메니저인지 다르다.
         * */
    }
}
