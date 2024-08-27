package com.mf.api.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Transferable {

	private String serviceId;
	private String name;
	private String imageUrl;
}
