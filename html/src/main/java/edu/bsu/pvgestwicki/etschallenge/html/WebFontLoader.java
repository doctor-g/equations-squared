package edu.bsu.pvgestwicki.etschallenge.html;

import java.util.Map;

import playn.core.util.Callback;

import com.google.common.collect.Maps;

import edu.bsu.pvgestwicki.etschallenge.core.util.FontLoader;

public final class WebFontLoader implements FontLoader {

	private static Map<String, WebFontLoader> map = Maps.newHashMap();

	public static WebFontLoader forFont(String name) {
		WebFontLoader loader = new WebFontLoader(name);
		map.put(name, loader);
		return loader;
	}

	public static void doneLoadingFont(String fontName) {
		if (!map.containsKey(fontName))
			throw new IllegalArgumentException("Not a recognized font name: "
					+ fontName);
		else
			map.get(fontName).success();
	}

	public static void failedToLoadFont(String fontName) {
		if (!map.containsKey(fontName))
			throw new IllegalArgumentException("Not a recognized font name: "
					+ fontName);
		else
			map.get(fontName).fail();
	}

	public static native void exportStaticMethods() /*-{
		$wnd.doneLoadingFont = $entry(@edu.bsu.pvgestwicki.etschallenge.html.WebFontLoader::doneLoadingFont(Ljava/lang/String;));
		$wnd.failedToLoadFont = $entry(@edu.bsu.pvgestwicki.etschallenge.html.WebFontLoader::failedToLoadFont(Ljava/lang/String;));
	}-*/;

	private static native void loadFonts() /*-{
		WebFontConfig = {
			google : {
				families : [ 'Droid Sans Mono', 'Droid Sans' ]
			},
			fontloading : function(fontFamily, fontDescription) {
				console.log("Loading font " + fontFamily + " ("
						+ fontDescription + ")");
			},
			fontactive : function(fontFamily, fontDescription) {
				console.log("Loaded " + fontFamily + " (" + fontDescription
						+ ")");
				window.doneLoadingFont(fontFamily);
			},
			fontinactive : function(fontFamily, fontDescription) {
				console.log("Failed to load " + fontFamily + " ("
						+ fontDescription + ")");
				window.failedToLoadFont(fontFamily);
			},
			inactive : function() {
				console.log("Did not load all fonts.");
			},
			active : function() {
				console.log("All done loading fonts.");
			}
		};
		(function() {
			var wf = document.createElement('script');
			wf.src = ('https:' == document.location.protocol ? 'https' : 'http')
					+ '://ajax.googleapis.com/ajax/libs/webfont/1/webfont.js';
			wf.type = 'text/javascript';
			wf.async = 'true';
			var s = document.getElementsByTagName('script')[0];
			s.parentNode.insertBefore(wf, s);
		})();
	}-*/;

	static {
		exportStaticMethods();
		loadFonts();
	}

	private final String name;
	private Callback<? super String> callback;
	private boolean done = false;

	private WebFontLoader(String name) {
		this.name = name;
	}

	public void addCallback(Callback<? super String> callback) {
		if (done) {
			callback.onSuccess(name);
		} else {
			this.callback = callback;
		}
	}

	private void success() {
		this.done = true;
		callback.onSuccess(name);
	}

	private void fail() {
		this.done = true;
		callback.onFailure(new FontLoadFailure());
	}

}

final class FontLoadFailure extends Throwable {
	private static final long serialVersionUID = -2592669467823835269L;
}