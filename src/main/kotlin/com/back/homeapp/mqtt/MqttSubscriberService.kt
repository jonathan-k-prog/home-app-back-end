package com.back.homeapp.mqtt

import com.back.homeapp.device.DeviceService
import com.back.homeapp.humidityReport.HumidityReportRequest
import com.back.homeapp.humidityReport.HumidityReportService
import com.back.homeapp.temperatureReport.TemperatureReportRequest
import com.back.homeapp.temperatureReport.TemperatureReportService
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
@ConditionalOnProperty(prefix = "mqtt", name = ["enabled"], havingValue = "true", matchIfMissing = true)
class MqttSubscriberService(
    private val mqttProperties: MqttProperties,
    private val objectMapper: ObjectMapper,
    private val humidityReportService: HumidityReportService,
    private val temperatureReportService: TemperatureReportService,
    private val deviceService: DeviceService,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private lateinit var client: MqttClient

    @PostConstruct
    fun start() {
        val runtimeClientId = "${mqttProperties.clientId}-${UUID.randomUUID()}"
        logger.info(
            "Starting MQTT client with broker={} topic={} clientId={}",
            mqttProperties.brokerUrl,
            mqttProperties.topic,
            runtimeClientId,
        )

        println("Starting MQTT client on topic ${mqttProperties.topic} via ${mqttProperties.brokerUrl}")

        client =
            MqttClient(
                mqttProperties.brokerUrl,
                runtimeClientId,
            )

        client.setCallback(
            object : MqttCallback {
                override fun connectionLost(cause: Throwable?) {
                    logger.warn(
                        "MQTT connection lost for broker={} topic={} clientId={}",
                        mqttProperties.brokerUrl,
                        mqttProperties.topic,
                        client.clientId,
                        cause,
                    )
                }

                override fun messageArrived(
                    topic: String,
                    message: MqttMessage,
                ) {
                    val payload = String(message.payload)
                    logger.info(
                        "MQTT message received on topic={} payload={}",
                        topic,
                        payload,
                    )
                    println("MQTT message received on $topic: $payload")
                    logger.debug(
                        "MQTT message received on topic={} payloadBytes={} clientConnected={}",
                        topic,
                        message.payload.size,
                        client.isConnected,
                    )
                    handleMessage(topic, payload)
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    // Subscriber only.
                }
            },
        )

        val options =
            MqttConnectOptions().apply {
                isAutomaticReconnect = true
                isCleanSession = false
            }

        logger.info(
            "Connecting to MQTT broker {} with clientId={} automaticReconnect={} cleanSession={}",
            mqttProperties.brokerUrl,
            client.clientId,
            options.isAutomaticReconnect,
            options.isCleanSession,
        )
        client.connect(options)
        logger.info(
            "Connected to MQTT broker {} with clientId={}",
            mqttProperties.brokerUrl,
            client.clientId,
        )

        client.subscribe(mqttProperties.topic)
        logger.info(
            "Subscribed to MQTT topic {} with clientId={}",
            mqttProperties.topic,
            client.clientId,
        )
    }

    private fun handleMessage(
        topic: String,
        payload: String,
    ) {
        try {
            val message = objectMapper.readValue(payload, SensorReadingMessage::class.java)
            val device = deviceService.findByIdentifierAndHomeIdentifier(message.deviceIdentifier, message.homeIdentifier)

            val humidityReport =
                HumidityReportRequest(
                    timestamp = Instant.now(),
                    value = message.humidity,
                    deviceId = device.id!!,
                )

            val temperatureReport =
                TemperatureReportRequest(
                    timestamp = Instant.now(),
                    value = message.temperature,
                    deviceId = device.id!!,
                )

            deviceService.markConnected(device.id!!)
            humidityReportService.create(humidityReport)
            temperatureReportService.create(temperatureReport)
        } catch (ex: Exception) {
            logger.error("Failed to process MQTT payload from topic {}: {}", topic, payload, ex)
        }
    }

    @PreDestroy
    fun stop() {
        if (!::client.isInitialized) {
            return
        }

        try {
            if (client.isConnected) {
                logger.info(
                    "Disconnecting MQTT client clientId={} from broker={}",
                    client.clientId,
                    mqttProperties.brokerUrl,
                )
                client.disconnect()
                logger.info(
                    "Disconnected MQTT client clientId={} from broker={}",
                    client.clientId,
                    mqttProperties.brokerUrl,
                )
            }
        } catch (ex: MqttException) {
            logger.warn("Error while disconnecting MQTT client", ex)
        } finally {
            client.close()
            logger.info("Closed MQTT client clientId={}", client.clientId)
        }
    }
}
