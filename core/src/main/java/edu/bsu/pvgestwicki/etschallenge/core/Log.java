package edu.bsu.pvgestwicki.etschallenge.core;

import tripleplay.util.Logger;

public final class Log {

	private static final Logger log = new Logger("algebra");

	public static Logger log() {
		return log;
	}

	private Log() {
	}
}
