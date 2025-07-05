package me.fucai.kafka_micrometer_playground

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KafkaMicrometerPlaygroundApplication

fun main(args: Array<String>) {
	runApplication<KafkaMicrometerPlaygroundApplication>(*args)
}
