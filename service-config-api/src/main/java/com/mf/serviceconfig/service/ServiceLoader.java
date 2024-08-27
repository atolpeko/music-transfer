package com.mf.serviceconfig.service;

import com.mf.serviceconfig.boot.config.properties.SpringServiceProperties;
import com.mf.serviceconfig.entity.Service;
import com.mf.serviceconfig.util.Tuple;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
public class ServiceLoader {

	private final SpringServiceProperties properties;

	public Tuple<List<Service>, List<Service>> load() {
		log.info("Loading config from {}", properties.getConfigLocation());
		try (var services = Files.walk(Paths.get(properties.getConfigLocation()))) {
			var config = services
				.filter(path -> path.toString().endsWith(".config"))
				.map(this::loadConfig)
				.toList();
			var sourceServices = config.stream()
				.filter(c -> c.get("source").equals("true"))
				.map(this::loadService)
				.toList();
			var targetServices = config.stream()
				.filter(c -> c.get("target").equals("true"))
				.map(this::loadService)
				.toList();

			return Tuple.of(sourceServices, targetServices);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Map<String, String> loadConfig(Path path) {
		try {
			var map = new HashMap<String, String>();
			var lines = Files.readAllLines(path);
			for (var line: lines) {
				var keyValue = line.split(" -> ");
				map.put(keyValue[0], keyValue[1]);
			}

			return map;
		} catch (NullPointerException e) {
			throw new RuntimeException("No config file found");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Service loadService(Map<String, String> config) {
		return Service.builder()
			.visibleName(config.get("visibleName"))
			.internalName(config.get("internalName"))
			.logoUrl(config.get("logoUrl"))
			.build();
	}
}
