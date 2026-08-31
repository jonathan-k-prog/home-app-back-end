package com.back.homeapp.mqtt

import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.whenever

class MqttPublisherServiceTest {
    private val properties = MqttProperties()

    private fun injectClient(
        service: MqttPublisherService,
        client: MqttClient,
    ) {
        val field = MqttPublisherService::class.java.getDeclaredField("client")
        field.isAccessible = true
        field.set(service, client)
    }

    @Test
    fun `publish sends the message when the client is connected`() {
        val service = MqttPublisherService(properties)
        val client = mock(MqttClient::class.java)
        whenever(client.isConnected).thenReturn(true)
        injectClient(service, client)

        service.publish("topic/one", "payload")

        val messageCaptor = argumentCaptor<MqttMessage>()
        verify(client).publish(org.mockito.kotlin.eq("topic/one"), messageCaptor.capture())
        assertEquals("payload", String(messageCaptor.firstValue.payload))
        assertEquals(1, messageCaptor.firstValue.qos)
    }

    @Test
    fun `publish uses the given qos`() {
        val service = MqttPublisherService(properties)
        val client = mock(MqttClient::class.java)
        whenever(client.isConnected).thenReturn(true)
        injectClient(service, client)

        service.publish("topic/one", "payload", qos = 2)

        val messageCaptor = argumentCaptor<MqttMessage>()
        verify(client).publish(org.mockito.kotlin.eq("topic/one"), messageCaptor.capture())
        assertEquals(2, messageCaptor.firstValue.qos)
    }

    @Test
    fun `publish does nothing when the client is not connected`() {
        val service = MqttPublisherService(properties)
        val client = mock(MqttClient::class.java)
        whenever(client.isConnected).thenReturn(false)
        injectClient(service, client)

        service.publish("topic/one", "payload")

        verify(client, never()).publish(any(), any<MqttMessage>())
    }

    @Test
    fun `stop returns immediately when the client was never started`() {
        val service = MqttPublisherService(properties)

        service.stop()
    }

    @Test
    fun `stop disconnects and closes a connected client`() {
        val service = MqttPublisherService(properties)
        val client = mock(MqttClient::class.java)
        whenever(client.isConnected).thenReturn(true)
        injectClient(service, client)

        service.stop()

        verify(client).disconnect()
        verify(client).close()
    }

    @Test
    fun `stop only closes a client that is already disconnected`() {
        val service = MqttPublisherService(properties)
        val client = mock(MqttClient::class.java)
        whenever(client.isConnected).thenReturn(false)
        injectClient(service, client)

        service.stop()

        verify(client, never()).disconnect()
        verify(client).close()
    }

    @Test
    fun `stop still closes the client when disconnect fails`() {
        val service = MqttPublisherService(properties)
        val client = mock(MqttClient::class.java)
        whenever(client.isConnected).thenReturn(true)
        whenever(client.disconnect()).thenThrow(MqttException(1))
        injectClient(service, client)

        service.stop()

        verify(client).close()
    }
}
