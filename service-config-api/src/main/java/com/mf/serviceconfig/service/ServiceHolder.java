package com.mf.serviceconfig.service;

import com.mf.serviceconfig.entity.Service;
import java.util.List;

public interface ServiceHolder {

	List<Service> getSourceServices();

	List<Service> getTargetServices();
}
