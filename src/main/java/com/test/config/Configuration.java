package com.test.config;

import java.util.HashMap;
import java.util.Map;

public class Configuration {

	private String jdbcDriver;

	private String jdbcUrl;

	private String jdbcUsername;

	private String jdbcPassword;

	private Map<String, MappedStatement> mappedstatements = new HashMap<String, MappedStatement>();

	public String getJdbcDriver() {
		return jdbcDriver;
	}

	public void setJdbcDriver(String jdbcDriver) {
		this.jdbcDriver = jdbcDriver;
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	public String getJdbcUsername() {
		return jdbcUsername;
	}

	public void setJdbcUsername(String jdbcUsername) {
		this.jdbcUsername = jdbcUsername;
	}

	public String getJdbcPassword() {
		return jdbcPassword;
	}

	public void setJdbcPassword(String jdbcPassword) {
		this.jdbcPassword = jdbcPassword;
	}

	public Map<String, MappedStatement> getMappedstatements() {
		return mappedstatements;
	}

	public void setMappedstatements(Map<String, MappedStatement> mappedstatements) {
		this.mappedstatements = mappedstatements;
	}
}
