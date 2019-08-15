package io.kauri.tutorial.springboot.simple.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.elastic.apm.opentracing.ElasticApmTracer;
import io.opentracing.Tracer;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class TracingConfiguration {

	@Bean
	public Tracer elasticApmTracer() {
		log.info("Configure ElasticApmTracer...");
		return new ElasticApmTracer();
	}
	
}
