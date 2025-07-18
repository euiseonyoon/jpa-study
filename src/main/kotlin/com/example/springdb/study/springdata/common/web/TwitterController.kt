package com.example.springdb.study.springdata.common.web

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class TwitterController(
    private val twitterRepository: TwitterRepository
) {
    @GetMapping("/post/{postIdOh}")
    fun getPost(@PathVariable("postIdOh") twit: Twitter): Twitter? {
        // DomainClassConverter 의 ToEntityConverter 나 ToIdConverter 이 내부적으로 동작
        // ToEntityConverter 내부에서 Twitter 도메인 repository 에서 findByIdInvoke()를 한다
        return twit
        // return twitterRepository.findById(id).orElse(null)
    }
}
