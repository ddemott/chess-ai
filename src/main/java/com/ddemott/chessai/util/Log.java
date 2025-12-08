package com.ddemott.chessai.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple logging wrapper to centralize logging behavior. Can be extended to
 * support different logging frameworks or config later.
 */
public final class Log {
	private static final Logger LOGGER = Logger.getLogger("com.ddemott.chessai");

	private Log() {
	}

	public static void info(String msg) {
		LOGGER.log(Level.INFO, msg);
	}

	public static void warn(String msg) {
		LOGGER.log(Level.WARNING, msg);
	}

	public static void error(String msg) {
		LOGGER.log(Level.SEVERE, msg);
	}

	public static void error(String msg, Throwable t) {
		LOGGER.log(Level.SEVERE, msg, t);
	}
}
