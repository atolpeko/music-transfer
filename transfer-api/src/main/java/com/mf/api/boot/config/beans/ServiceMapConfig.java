package com.mf.api.boot.config.beans;

import com.mf.api.port.MusicServicePort;
import com.mf.api.usecase.valueobject.ServiceMap;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceMapConfig {

	@Bean
	public ServiceMap serviceMap(List<MusicServicePort> services) {
		var map = services.stream()
			.collect(Collectors.toMap(Object::toString, Function.identity()));
		return new ServiceMap(map);
	}
}
