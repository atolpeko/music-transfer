package com.mf.queue.config;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

public class RequestQueueConfigurator {

	private RequestQueueConfigurator() {}

	public static void setLogLevel(Level level) {
		Configurator.setLevel("com.mf.queue", level);
	}
}
