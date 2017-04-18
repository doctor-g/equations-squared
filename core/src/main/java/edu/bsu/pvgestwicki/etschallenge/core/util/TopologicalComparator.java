package edu.bsu.pvgestwicki.etschallenge.core.util;

import java.util.Comparator;

import pythagoras.i.IPoint;

public final class TopologicalComparator implements Comparator<IPoint> {

	private final static TopologicalComparator INSTANCE= new TopologicalComparator();
	
	public static TopologicalComparator instance() {
		return INSTANCE;
	}
	
	private TopologicalComparator() {}
	
	@Override
	public int compare(IPoint o1, IPoint o2) {
		return o1.x() + o1.y() - o2.x() - o2.y();
	}

	

}
