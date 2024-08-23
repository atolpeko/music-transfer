package com.mf.serviceconfig.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Service {

	private String visibleName;
	private String internalName;
	private String logoUrl;
}
