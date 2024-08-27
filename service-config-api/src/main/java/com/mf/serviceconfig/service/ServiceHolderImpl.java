package com.mf.serviceconfig.service;

import com.mf.serviceconfig.entity.Service;

import java.util.List;

public class ServiceHolderImpl implements ServiceHolder {

	private static List<Service> sourceServices;
	private static List<Service> targetServices;

	public ServiceHolderImpl(ServiceLoader loader) {
		var services = loader.load();
		sourceServices = services.getFirst();
		targetServices = services.getSecond();
	}

	@Override
	public List<Service> getSourceServices() {
		return sourceServices;
	}

	@Override
	public List<Service> getTargetServices() {
		return targetServices;
	}
}
