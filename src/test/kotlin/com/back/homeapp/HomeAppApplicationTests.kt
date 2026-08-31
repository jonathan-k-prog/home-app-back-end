package com.back.homeapp

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    classes = [HomeAppApplication::class],
    properties = ["mqtt.enabled=false"],
)
class HomeAppApplicationTests {
    @Test
    fun contextLoads() {
    }
}
