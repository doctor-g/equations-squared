package edu.bsu.pvgestwicki.etschallenge.core.util;

import playn.core.util.Callback;

public interface FontLoader {

	void addCallback(Callback<? super String> callback);
	
}
