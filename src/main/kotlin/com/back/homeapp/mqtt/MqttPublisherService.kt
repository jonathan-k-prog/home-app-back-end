package com.back.homeapp.mqtt

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import java.util.UUID

@Service
@ConditionalOnProperty(prefix = "mqtt", name = ["enabled"], havingValue = "true", matchIfMissing = true)
class MqttPublisherService(
    private val mqttProperties: MqttProperties,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private lateinit var client: MqttClient

    @PostConstruct
    fun start() {
        val clientId = "${mqttProperties.clientId}-publisher-${UUID.randomUUID()}"
        client = MqttClient(mqttProperties.brokerUrl, clientId)

        val options =
            MqttConnectOptions().apply {
                isAutomaticReconnect = true
                isCleanSession = true
            }

        client.connect(options)
        logger.info("MQTT publisher connected to {} with clientId={}", mqttProperties.brokerUrl, clientId)
    }

    fun publish(
        topic: String,
        payload: String,
        qos: Int = 1,
    ) {
        if (!client.isConnected) {
            logger.warn("MQTT publisher not connected, skipping publish to topic={}", topic)
            return
        }

        val message =
            MqttMessage(payload.toByteArray()).apply {
                this.qos = qos
            }

        client.publish(topic, message)
        logger.info("MQTT published to topic={} payload={}", topic, payload)
    }

    @PreDestroy
    fun stop() {
        if (!::client.isInitialized) return
        try {
            if (client.isConnected) client.disconnect()
        } catch (ex: MqttException) {
            logger.warn("Error while disconnecting MQTT publisher", ex)
        } finally {
            client.close()
        }
    }
}
