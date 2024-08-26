package com.mf.serviceconfig.rest.controller;

import com.mf.serviceconfig.rest.ServiceConfigAPI;
import com.mf.serviceconfig.rest.entity.Response;
import com.mf.serviceconfig.service.ServiceHolder;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
public class ServiceConfigController implements ServiceConfigAPI {

	private final ServiceHolder serviceHolder;

	@Override
	public Response get() {
		return new Response(
			serviceHolder.getSourceServices(),
			serviceHolder.getTargetServices()
		);
	}
}
