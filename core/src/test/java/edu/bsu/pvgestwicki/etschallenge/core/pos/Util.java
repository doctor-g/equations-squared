package edu.bsu.pvgestwicki.etschallenge.core.pos;

import java.util.List;

import pythagoras.i.IPoint;

import com.google.common.collect.Lists;

public final class Util {
	private Util() {
	}

	public static List<IPoint> makeList(IPoint start, Direction direction,
			int size) {
		List<IPoint> list = Lists.newArrayList();
		IPoint p = start;
		for (int i = 0; //
		i < size; //
		i++, p = direction.increasingFunction().apply(p)) {
			list.add(p);
		}
		return list;
	}
}
