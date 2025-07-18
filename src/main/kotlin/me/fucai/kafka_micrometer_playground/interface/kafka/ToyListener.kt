package me.fucai.kafka_micrometer_playground.`interface`.kafka

import io.github.oshai.kotlinlogging.KotlinLogging
import io.micrometer.tracing.BaggageManager
import io.micrometer.tracing.Tracer
import me.fucai.kafka_micrometer_playground.`interface`.Constants
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class ToyListener(
    private val tracer: Tracer,
    //private val baggageManager: BaggageManager
) {
    private val log = KotlinLogging.logger { }

    @KafkaListener(
        topics = [Constants.TOY_TOPIC_NAME], groupId = "toyGroup",
        concurrency = "2", clientIdPrefix = "toyListener", autoStartup = "true"
    )
    fun listen(records: List<ConsumerRecord<String, String>>) {

        log.info { "trace context now is ${tracer.currentTraceContext().context()}" }



        records.forEach { record ->

            val messageID = record.headers().lastHeader(Constants.MESSAGE_HEADER_ID)
                .value().toString(Charsets.UTF_8)
            val span = tracer.nextSpan().name("process-message")
            span.tag(Constants.MESSAGE_HEADER_ID, messageID)

            try {
                tracer.withSpan(span).use {
                    log.info { " Current context: " + tracer.currentTraceContext().context() }
                    log.info {
                        "header ID : " +
                                messageID +
                                "value : ${record.value()}"
                    }
                }
            } finally {
                span.end()
            }


//            val s = tracer.startScopedSpan("my-span").apply {
//                tracer.createBaggageInScope(
//                    tracer.currentTraceContext().context()!!,
//                    Constants.MESSAGE_HEADER_ID,
//                    messageID
//                )
//            }
//            log.info { " Current context: " + tracer.currentTraceContext() }
//            log.info {
//                "header ID : " +
//                        messageID +
//                        "value : ${record.value()}"
//            }
//            s.end()
        }
    }

}