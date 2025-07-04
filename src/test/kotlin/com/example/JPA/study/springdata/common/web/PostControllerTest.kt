package com.example.JPA.study.springdata.common.web

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var twitterRepository: TwitterRepository

    @Test
    fun test() {
        val twitter = Twitter(title = "twit twit")
        val id = twitterRepository.save(twitter).id

        mockMvc.perform(get("/post/$id"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("twit twit"))
            .andExpect(jsonPath("$.id").value(id.toString()))

    }
}
