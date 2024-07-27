package com.mf.auth.boot.config.beans;

import com.mf.auth.port.MusicServicePort;
import com.mf.auth.usecase.valueobject.ServiceMap;

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
