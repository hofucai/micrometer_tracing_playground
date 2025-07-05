package me.fucai.kafka_micrometer_playground.`interface`.rest

import io.github.oshai.kotlinlogging.KotlinLogging
import me.fucai.kafka_micrometer_playground.`interface`.Constants
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.http.ResponseEntity
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import kotlin.random.Random

@RestController
@RequestMapping("toyMessageBatch")
/**
 * Just a toy REST controller to trigger sending messages onto the kafka topic
 */
class RestEntryPointController(
    private val kTemplate: KafkaTemplate<String, String>
) {
    val log = KotlinLogging.logger { }

    private val randomWords = listOf(
        "astronaut",  // A
        "bicycle",    // B
        "canyon",     // C
        "dolphin",    // D
        "elephant",   // E
        "falcon",     // F
        "giraffe",    // G
        "horizon",    // H
        "igloo",      // I
        "jungle",     // J
        "koala",      // K
        "lantern",    // L
        "mountain",   // M
        "nectar",     // N
        "oasis",      // O
        "penguin",    // P
        "quartz",     // Q
        "rainbow",    // R
        "satellite",  // S
        "tornado",    // T
        "umbrella",   // U
        "volcano",    // V
        "whistle",    // W
        "xylophone",  // X
        "yogurt",     // Y
        "zebra"       // Z
    )


    @PostMapping
    fun publishMessage(
        @RequestParam numberOfMessages: Int
    ): ResponseEntity<Unit> {
        try {
            repeat(numberOfMessages) {
                kTemplate.send(producerRecordFactory())
            }
        } catch (ex: Exception) {
            log.error(ex) { "Cannot send payloads to topic" }
            return ResponseEntity.internalServerError().build()
        }
        return ResponseEntity.accepted().build()
    }

    fun producerRecordFactory(): ProducerRecord<String, String> {
        val word = randomWords[Random.nextInt(randomWords.size)]
        return ProducerRecord<String, String>(Constants.TOY_TOPIC_NAME, word).apply {
            headers().add(
                Constants.MESSAGE_HEADER_ID, UUID.randomUUID().toString()
                    .toByteArray(Charsets.UTF_8)
            )
        }


    }


}