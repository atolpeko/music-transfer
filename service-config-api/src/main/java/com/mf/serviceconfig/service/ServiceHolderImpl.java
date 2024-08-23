package com.mf.serviceconfig.service;

import com.mf.serviceconfig.entity.Service;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServiceHolderImpl implements ServiceHolder {

	private static final List<Service> SERVICES = ServiceLoader.load();

	@Override
	public List<Service> get() {
		return SERVICES;
	}
}
