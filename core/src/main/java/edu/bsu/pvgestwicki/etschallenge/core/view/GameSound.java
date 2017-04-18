package edu.bsu.pvgestwicki.etschallenge.core.view;

import static playn.core.PlayN.assets;
import playn.core.Sound;

public enum GameSound {

	CLICK("sound/click"),
	WHOOSH("sound/whoosh"),
	BADGE("sound/badge"),
	DEMERIT("sound/demerit"),
	BLIP("sound/blip"),
	PLACE("sound/place");

	public final Sound sound;

	private GameSound(String path) {
		sound = assets().getSound(path);
	}

}
