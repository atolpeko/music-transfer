package com.mf.api.boot.config;

import java.time.Duration;
import java.util.Collections;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

	@Bean
	public RestTemplate restTemplate() {
		var converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(
			Collections.singletonList(MediaType.APPLICATION_FORM_URLENCODED));

		var template = new RestTemplateBuilder()
			.setConnectTimeout(Duration.ofSeconds(5))
			.setReadTimeout(Duration.ofSeconds(5))
			.build();
		template.getMessageConverters().add(converter);
		return template;
	}
}
