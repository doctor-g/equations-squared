package edu.bsu.pvgestwicki.etschallenge.core.util;

import playn.core.util.Callback;

public abstract class UnfailableCallback<T> implements Callback<T>{

	@Override
	public final void onFailure(Throwable cause) {
		throw new IllegalStateException();
	}
	
}
