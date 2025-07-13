package com.example.JPA.study.springtx.order

import com.example.JPA.study.logger
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class OrderServiceTest {
    private val log = logger()

    @Autowired
    lateinit var orderService: OrderService
    @Autowired
    lateinit var orderRepository: OrderRepository

    @Test
    fun order() {
        // Given
        val order = Order(processCase = "정상")

        // When
        val savedOrder = orderService.order(order)

        // Then
        val foundOrder = orderRepository.findById(savedOrder.id!!).get()
        assertTrue { foundOrder.payStatus == "완료" }
    }

    @Test
    fun runtimeException() {
        // Given
        val order = Order(processCase = "예외")

        assertThrows<RuntimeException>{
            orderService.order(order)
        }

        // Then
        // 롤백되었기 때문에 없어야 한다.
        val foundOrder = orderRepository.findById(order.id!!)
        assertTrue { foundOrder.isEmpty }
    }

    @Test
    fun businessException() {
        // Given
        val order = Order(processCase = "잔고부족")

        // When
        try {
            orderService.order(order)
        } catch (e: NotEnoughMoneyException) {
            log.info("고객에게 잔고부족이라고 알려주세요~~")
        }

        // Then
        val foundOrder = orderRepository.findById(order.id!!).get()
        assertTrue { foundOrder.payStatus == "대기" }
    }
}
