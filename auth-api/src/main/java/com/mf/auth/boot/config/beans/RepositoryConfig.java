package com.mf.auth.boot.config.beans;

import com.mf.auth.adapter.out.repository.JWTRepositoryJdbcAdapter;
import com.mf.auth.adapter.out.repository.UUIDRepositoryJdbcAdapter;
import com.mf.auth.port.JWTRepositoryPort;
import com.mf.auth.port.UUIDRepositoryPort;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class RepositoryConfig {

	@Bean
	public UUIDRepositoryPort uuidRepository(JdbcTemplate jdbc) {
		return new UUIDRepositoryJdbcAdapter(jdbc);
	}

	@Bean
	public JWTRepositoryPort jwtRepository(JdbcTemplate jdbc) {
		return new JWTRepositoryJdbcAdapter(jdbc);
	}
}
