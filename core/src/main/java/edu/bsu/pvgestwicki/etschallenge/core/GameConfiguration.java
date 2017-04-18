package edu.bsu.pvgestwicki.etschallenge.core;

import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import pythagoras.f.IRectangle;
import pythagoras.f.Rectangle;
import tripleplay.anim.Animator;
import edu.bsu.pvgestwicki.etschallenge.core.view.TileImageFactory;

public abstract class GameConfiguration {

	public static final GameConfiguration instance() {
		return SINGLETON;
	}

	private static final GameConfiguration SINGLETON = new GameConfiguration() {

		private final IRectangle tileTrayBounds = new Rectangle(10, 500, 500,
				60);
		private final IDimension tileSizeInTray = new Dimension(50, 50);
		private final IRectangle equalsTrayBounds = new Rectangle(525, 500, 60,
				60);
		private final Animator animator = new Animator();

		@Override
		public IRectangle tileTrayBounds() {
			return tileTrayBounds;
		}

		@Override
		public IDimension tileSizeInTray() {
			return tileSizeInTray;
		}

		@Override
		public IRectangle equalsTrayBounds() {
			return equalsTrayBounds;
		}

		@Override
		public Animator animator() {
			return animator;
		}

		@Override
		public float tileToBoardAnimationDuration() {
			return 0.15f;
		}

		@Override
		public float tileToTrayAnimationDuration() {
			return 0.25f;
		}

		@Override
		public int digitsInTray() {
			return 6;
		}

		@Override
		public int operatorsInTray() {
			return 2;
		}

		@Override
		public TileImageFactory tileImageFactory() {
			return TileImageFactory.instance();
		}
	};

	public abstract IRectangle tileTrayBounds();

	public final int tilesAllowedInTray() {
		return digitsInTray() + operatorsInTray();
	}

	public abstract IDimension tileSizeInTray();

	public abstract IRectangle equalsTrayBounds();

	public abstract Animator animator();

	public abstract float tileToBoardAnimationDuration();

	public abstract float tileToTrayAnimationDuration();

	public abstract int digitsInTray();

	public abstract int operatorsInTray();

	public abstract TileImageFactory tileImageFactory();
}
