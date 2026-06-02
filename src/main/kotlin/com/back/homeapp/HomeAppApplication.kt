package com.back.homeapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class HomeAppApplication

fun main(args: Array<String>) {
    runApplication<HomeAppApplication>(*args)
}
