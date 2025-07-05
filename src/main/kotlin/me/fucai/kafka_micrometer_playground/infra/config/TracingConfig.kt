package me.fucai.kafka_micrometer_playground.infra.config

import io.micrometer.tracing.Tracer
import io.micrometer.tracing.otel.bridge.OtelBaggageManager
import io.micrometer.tracing.otel.bridge.OtelCurrentTraceContext
import io.micrometer.tracing.otel.bridge.OtelTracer
import io.micrometer.tracing.otel.bridge.Slf4JBaggageEventListener
import io.micrometer.tracing.otel.bridge.Slf4JEventListener
import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.sdk.OpenTelemetrySdk
import io.opentelemetry.sdk.trace.SdkTracerProvider
import io.opentelemetry.sdk.trace.samplers.Sampler
import me.fucai.kafka_micrometer_playground.`interface`.Constants
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TracingConfig {

    private val baggageKeys = listOf(Constants.MESSAGE_HEADER_ID)
    @Bean
    fun otelSdkTracerProvider(): SdkTracerProvider {

        return SdkTracerProvider.builder()
            .setSampler(Sampler.alwaysOn())
            .build()
    }

    @Bean
    fun openTelemetry(sdkTracerProvider: SdkTracerProvider): OpenTelemetry {
        return OpenTelemetrySdk.builder()
            .setTracerProvider(sdkTracerProvider)
            .build()
    }

    @Bean
    fun otelCurrentTraceContext(): OtelCurrentTraceContext {
        return OtelCurrentTraceContext()
    }

    @Bean
    fun slf4JEventListener() = Slf4JEventListener()

    @Bean
    fun slf4JBaggageEventListener() = Slf4JBaggageEventListener(baggageKeys)

    @Bean
    fun micrometerTracer(openTelemetry: OpenTelemetry): Tracer {

        val rawOtelTracer = openTelemetry(otelSdkTracerProvider())
            .tracerProvider.get("io.micrometer.micrometer-tracing");

        return OtelTracer(rawOtelTracer, otelCurrentTraceContext(), { e ->
            slf4JEventListener().onEvent(e)
            slf4JBaggageEventListener().onEvent(e)
        },  OtelBaggageManager(otelCurrentTraceContext(),
            emptyList(), baggageKeys))
    }
}