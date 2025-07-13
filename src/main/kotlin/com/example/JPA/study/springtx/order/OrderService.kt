package com.example.JPA.study.springtx.order

import com.example.JPA.study.logger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderRepository: OrderRepository
) {
    private val log = logger()

    @Transactional
    fun order(order: Order): Order {
        log.info("order 호출")
        val savedOrder = orderRepository.save(order)

        log.info("결제 프로세스 시작")

        if(order.processCase == "예외") {
            log.info("시스템 예외 발생")
            throw RuntimeException("시스템 예외 발생")
        } else if(order.processCase == "잔고부족") {
            log.info("잔고부족 비지니스 예외 발생")
            order.payStatus = "대기"
            throw NotEnoughMoneyException("잔고가 부족해요.")
        } else {
            log.info("정상 승인")
            order.payStatus = "완료"
        }

        log.info("결제 프로세스 끝")
        return savedOrder
    }
}
