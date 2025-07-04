package com.example.JPA.study

import com.example.JPA.study.repositories.custom.PostRepository
import com.example.JPA.study.repositories.models.Post
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class PostCustomRepoTest1 {

    @Autowired
    lateinit var postRepository: PostRepository

    @Test
    fun test() {
        postRepository.findAllPost()
    }

    @Test
    fun test2() {
        val post = Post()
        post.title = "post title"
        post.content = "post content"

        // 이건 insert 쿼리 안날아감 -> DataJpaTest가 @Transactional 이라 해당 테스트 종료시 rollback함
        postRepository.save(post)
    }

    @Test
    fun test3() {
        val post = Post()
        post.title = "post title"
        post.content = "post content"

        postRepository.save(post)
        // 이렇게 하면 위의 insert, 아래의 select 쿼리가 날아감
        postRepository.findAllPost()
    }

    @Test
    fun test4() {
        val post = Post()
        post.title = "post title"
        post.content = "post content"

        postRepository.save(post)
        // 이렇게 하면 위의 insert, 아래의 select 쿼리가 날아감
        postRepository.findAllPost()

        // flush 까지 해줘야 실제 DELETE 쿼리가 날아가는걸 확인할 수 있다.
        // 이유는 위와 같음
        postRepository.delete(post)
        postRepository.flush()
    }
}
