package com.mf.serviceconfig.service;

import com.mf.serviceconfig.entity.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ServiceLoader {

	private static final String SERVICE_DIR;

	static {
		SERVICE_DIR = ServiceLoader.class.getResource("/services").getPath();
	}

	public static List<Service> load() {
		try (var services = Files.walk(Paths.get(SERVICE_DIR))) {
			return services
				.filter(path -> path.toString().endsWith(".config"))
				.map(ServiceLoader::loadService)
				.toList();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Service loadService(Path path) {
		try {
			var yaml = loadYaml(path);
			return Service.builder()
				.visibleName(yaml.get("visibleName"))
				.internalName(yaml.get("internalName"))
				.logoUrl(yaml.get("logoUrl"))
				.build();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Map<String, String> loadYaml(Path path) throws IOException {
		try {
			var map = new HashMap<String, String>();
			var lines = Files.readAllLines(path);
			for (var line: lines) {
				var keyValue = line.split(" -> ");
				map.put(keyValue[0], keyValue[1]);
			}

			return map;
		} catch (NullPointerException e) {
			throw new IOException("No config file found");
		}
	}
}
