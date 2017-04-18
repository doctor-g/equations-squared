package edu.bsu.pvgestwicki.etschallenge.core.pos;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.bsu.pvgestwicki.etschallenge.core.pos.DirectionFunction.ABOVE;
import static edu.bsu.pvgestwicki.etschallenge.core.pos.DirectionFunction.BELOW;
import static edu.bsu.pvgestwicki.etschallenge.core.pos.DirectionFunction.LEFT;
import static edu.bsu.pvgestwicki.etschallenge.core.pos.DirectionFunction.RIGHT;

import java.util.Collection;
import java.util.Set;

import pythagoras.i.IPoint;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;

public final class HasThreeFreedomsPredicate implements Predicate<IPoint> {

	public static HasThreeFreedomsPredicate createWithSourceData(
			Set<IPoint> sourceData) {
		checkNotNull(sourceData);
		return new HasThreeFreedomsPredicate(sourceData);
	}

	private final Collection<IPoint> sourceData;

	private HasThreeFreedomsPredicate(Collection<IPoint> sourceData) {
		this.sourceData = ImmutableSet.copyOf(sourceData);
	}

	@Override
	public boolean apply(IPoint input) {
		int horizNeighbors = countNeighborsAlongAxis(LEFT, RIGHT, input);
		int vertNeighbors = countNeighborsAlongAxis(ABOVE, BELOW, input);
		return horizNeighbors + vertNeighbors == 1;
	}

	private int countNeighborsAlongAxis(DirectionFunction f1, DirectionFunction f2, IPoint input) {
		int count = 0;
		if (sourceData.contains(f1.apply(input))) count++;
		if (sourceData.contains(f2.apply(input))) count++;
		return count;
	}
	

}
