package com.example.JPA.study

import com.example.JPA.study.repositories.customgeneral.MyRepositoryImpl
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
// NOTE: 이거 중요!!
@EnableJpaRepositories(repositoryBaseClass = MyRepositoryImpl::class)
class JpaStudyApplication

fun main(args: Array<String>) {
	runApplication<JpaStudyApplication>(*args)
}
