package edu.bsu.pvgestwicki.etschallenge.core;

import java.util.List;
import java.util.Set;

import playn.core.Image;
import playn.core.PlayN;
import playn.core.util.Callback;
import react.Value;
import react.ValueView;
import react.ValueView.Listener;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import edu.bsu.pvgestwicki.etschallenge.core.util.FontLoader;
import edu.bsu.pvgestwicki.etschallenge.core.view.GameImage;

public final class AssetLoadingTracker {

	public static final class Builder {
		private final List<Image> imageList = Lists.newArrayList();
		private List<FontLoader> fontLoaders;
		private int totalAssetsToTrack = 0;

		public Builder withFontLoader(FontLoader... fontLoaders) {
			totalAssetsToTrack += fontLoaders.length;
			this.fontLoaders = ImmutableList.copyOf(fontLoaders);
			return this;
		}

		public Builder withImages(GameImage... images) {
			totalAssetsToTrack += images.length;
			for (GameImage image : images) {
				imageList.add(image.image);
			}
			return this;
		}

		public AssetLoadingTracker build() {
			AssetLoadingTracker tracker = new AssetLoadingTracker(
					totalAssetsToTrack);
			tracker.trackImages(imageList);
			if (fontLoaders != null)
				tracker.trackFonts(fontLoaders);
			return tracker;
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	private final Set<Callback<?>> unresolvedCallbacks = Sets.newHashSet();

	private final int totalNumberOfAssetsTracked;
	private final Value<Integer> assetsRemaining;

	private AssetLoadingTracker(int totalNumberOfAssetsTracked) {
		this.totalNumberOfAssetsTracked = totalNumberOfAssetsTracked;
		this.assetsRemaining = Value.create(totalNumberOfAssetsTracked);
		initLogging();
	}

	private void initLogging() {
		assetsRemaining.connect(new Listener<Integer>() {
			@Override
			public void onChange(Integer value, Integer oldValue) {
				PlayN.log().info("Assets remaining: " + Integer.valueOf(value));
			}
		});
	}

	public int totalNumberOfAssetsTracked() {
		return totalNumberOfAssetsTracked;
	}

	public ValueView<Integer> assetsRemaining() {
		return assetsRemaining;
	}

	private void trackImages(List<Image> images) {
		for (Image image : images)
			image.addCallback(createRegisteredCallback());
	}

	private void trackFonts(List<FontLoader> fontLoaders) {
		for (FontLoader fontLoader : fontLoaders) {
			fontLoader.addCallback(createRegisteredCallback());
		}
	}

	private <T> RemoveOnResolveCallback<T> createRegisteredCallback() {
		RemoveOnResolveCallback<T> callback = new RemoveOnResolveCallback<T>();
		unresolvedCallbacks.add(callback);
		return callback;
	}

	private final class RemoveOnResolveCallback<T> implements Callback<T> {

		@Override
		public void onSuccess(T resource) {
			cleanup();
		}

		@Override
		public void onFailure(Throwable err) {
			PlayN.log().warn("Failed to load asset.", err);
			cleanup();
		}

		private void cleanup() {
			unresolvedCallbacks.remove(this);
			assetsRemaining.update(assetsRemaining.get() - 1);
		}
	}

}
