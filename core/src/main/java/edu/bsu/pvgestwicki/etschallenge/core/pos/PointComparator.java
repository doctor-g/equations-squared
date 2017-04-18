package edu.bsu.pvgestwicki.etschallenge.core.pos;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Comparator;

import pythagoras.i.IPoint;

public enum PointComparator implements Comparator<IPoint> {

	LEFTWARD(new Comparator<IPoint>() {

		@Override
		public int compare(IPoint o1, IPoint o2) {
			return o2.x() - o1.x();
		}

	}),

	RIGHTWARD(new Comparator<IPoint>() {

		@Override
		public int compare(IPoint o1, IPoint o2) {
			return o1.x() - o2.x();
		}

	}),

	UPWARD(new Comparator<IPoint>() {

		@Override
		public int compare(IPoint o1, IPoint o2) {
			return o2.y() - o1.y();
		}

	}),

	DOWNWARD(new Comparator<IPoint>() {

		@Override
		public int compare(IPoint o1, IPoint o2) {
			return o1.y() - o2.y();
		}

	});

	private Comparator<IPoint> delegate;

	private PointComparator(Comparator<IPoint> c) {
		this.delegate = checkNotNull(c);
	}

	@Override
	public int compare(IPoint arg0, IPoint arg1) {
		return delegate.compare(arg0, arg1);
	}

}
