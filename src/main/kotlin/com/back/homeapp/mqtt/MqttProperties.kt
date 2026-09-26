package com.back.homeapp.mqtt

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Configuration properties for MQTT.
 *
 * @property enabled Whether MQTT is enabled. Default is true.
 * @property brokerUrl The URL of the MQTT broker. Default is "tcp://localhost:1883".
 * @property clientId The client ID for the MQTT connection. Default is "homeapp-subscriber".
 * @property topic The topic to subscribe to. Default is "esp32/dht11".
 */
@ConfigurationProperties(prefix = "mqtt")
data class MqttProperties(
    val enabled: Boolean = true,
    val brokerUrl: String = "tcp://localhost:1883",
    val clientId: String = "homeapp-subscriber",
    val topic: String = "esp32/dht11",
)
