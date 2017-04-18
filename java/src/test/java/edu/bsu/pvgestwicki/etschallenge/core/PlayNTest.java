package edu.bsu.pvgestwicki.etschallenge.core;

import playn.java.JavaPlatform;

public abstract class PlayNTest {

	static {
		JavaPlatform platform = JavaPlatform.register();
		String packageName = PlayNTest.class.getPackage().getName();
		String pathNameFromPackage = packageName.replaceAll("\\.", "/");
		String assetPathPrefix = pathNameFromPackage + "/resources";
		platform.assets().setPathPrefix(assetPathPrefix);
	}
}
