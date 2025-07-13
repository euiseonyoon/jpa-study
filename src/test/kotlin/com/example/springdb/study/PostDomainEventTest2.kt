package com.example.springdb.study

import com.example.springdb.study.springdata.common.repositories.custom.PostRepository
import com.example.springdb.study.springdata.common.repositories.domainevent.PostListenerConfig
import com.example.springdb.study.springdata.common.repositories.models.Post
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import

@DataJpaTest
@Import(PostListenerConfig::class)
class PostDomainEventTest2 {

    @Autowired
    lateinit var postRepository: PostRepository

    @Test
    fun test1() {
        val post = Post()
        post.title = "title"
        post.content = "content"

        /***
         * https://docs.spring.io/spring-data/cassandra/reference/repositories/core-domain-events.html#page-title
         * Post가 AbstractAggregateRoot를 상속
         * AbstractAggregateRoot내부에
         * @DomainEvents
         * 	protected Collection<Object> domainEvents() {
         * 		return Collections.unmodifiableList(domainEvents);
         * 	} 에 EVENT가 저장된다.
         *
         * 	save(), saveAll(), delete(), deleteAll(), deleteAllInBatch(), deleteInBatch() 일때만 event 발생하는것 같다.
         */
        postRepository.save(post.publish())
        postRepository.flush()
    }

    // 아래처럼 실제로 insert 쿼리가 발생하지 않았는데 (commit이 일어나지 않았는데)에도 이벤트가 발생한다.
    @Test
    fun test2() {
        val post = Post()
        post.title = "title"
        post.content = "content"
        postRepository.save(post.publish())
    }
}
