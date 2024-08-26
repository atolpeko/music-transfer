package com.mf.serviceconfig.service;

import com.mf.serviceconfig.entity.Service;
import com.mf.serviceconfig.util.Tuple;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServiceHolderImpl implements ServiceHolder {

	private static final Tuple<List<Service>, List<Service>> SERVICES = ServiceLoader.load();

	@Override
	public List<Service> getSourceServices() {
		return SERVICES.getFirst();
	}

	@Override
	public List<Service> getTargetServices() {
		return SERVICES.getSecond();
	}
}
