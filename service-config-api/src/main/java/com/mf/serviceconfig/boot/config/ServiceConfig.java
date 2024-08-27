package com.mf.serviceconfig.boot.config;

import com.mf.serviceconfig.boot.config.properties.SpringServiceProperties;
import com.mf.serviceconfig.service.ServiceHolder;
import com.mf.serviceconfig.service.ServiceHolderImpl;

import com.mf.serviceconfig.service.ServiceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

	@Bean
	public ServiceHolder serviceHolder(ServiceLoader loader) {
		return new ServiceHolderImpl(loader);
	}

	@Bean
	public ServiceLoader serviceLoader(SpringServiceProperties properties) {
		return new ServiceLoader(properties);
	}
}
