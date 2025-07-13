package com.example.springdb.study

import com.example.springdb.study.springdata.common.querydsl.Post2
import com.example.springdb.study.springdata.common.querydsl.Post2Repository
import com.example.springdb.study.springdata.common.querydsl.QPost2
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.util.Optional
import kotlin.test.assertTrue

@DataJpaTest
class QueryDslTest2 {

    @Autowired
    lateinit var post2Repository: Post2Repository

    @Test
    fun test() {
        val post = Post2()
        post.title = "title"
        post.content = "content"
        post2Repository.save(post)

        val predicate = QPost2.post2
            .title.startsWith("ti")
            .and(
                QPost2.post2.content.containsIgnoreCase("tent")
            )

        val result: Optional<Post2> = post2Repository.findOne(predicate)

        assertTrue { result.isPresent }
    }
}
