package com.back.homeapp.mqtt

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "mqtt")
data class MqttProperties(
    val enabled: Boolean = true,
    val brokerUrl: String = "tcp://localhost:1883",
    val clientId: String = "homeapp-subscriber",
    val topic: String = "esp32/dht11"
)
