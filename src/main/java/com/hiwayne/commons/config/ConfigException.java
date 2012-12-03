package com.hiwayne.commons.config;

public class ConfigException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3104860824777269954L;

	public ConfigException() {
	}

	public ConfigException(String message) {
		super(message);
	}

	public ConfigException(Throwable cause) {
		super(cause);
	}

	public ConfigException(String message, Throwable cause) {
		super(message, cause);

	}

}
