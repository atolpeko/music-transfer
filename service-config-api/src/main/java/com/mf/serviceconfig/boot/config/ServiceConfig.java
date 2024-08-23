package com.mf.serviceconfig.boot.config;

import com.mf.serviceconfig.service.ServiceHolder;
import com.mf.serviceconfig.service.ServiceHolderImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

	@Bean
	public ServiceHolder serviceHolder() {
		return new ServiceHolderImpl();
	}
}
