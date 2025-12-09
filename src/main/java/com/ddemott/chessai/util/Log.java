package com.ddemott.chessai.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple logging wrapper to centralize logging behavior. Can be extended to
 * support different logging frameworks or config later.
 */
public final class Log {
	private static final Logger LOGGER = LoggerFactory.getLogger("com.ddemott.chessai");

	private Log() {
	}

	public static void info(String msg) {
		LOGGER.info(msg);
	}

	public static void warn(String msg) {
		LOGGER.warn(msg);
	}

	public static void error(String msg) {
		LOGGER.error(msg);
	}

	public static void error(String msg, Throwable t) {
		LOGGER.error(msg, t);
	}
}
