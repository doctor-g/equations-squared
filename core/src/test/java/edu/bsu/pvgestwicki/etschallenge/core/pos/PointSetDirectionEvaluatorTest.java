package edu.bsu.pvgestwicki.etschallenge.core.pos;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.List;

import org.junit.Test;

import pythagoras.i.IPoint;
import pythagoras.i.Point;

import com.google.common.collect.ImmutableSet;

public class PointSetDirectionEvaluatorTest {

	private static final IPoint ORIGIN = new Point(0, 0);

	private PointSetDirectionEvaluator eval = createTurnTopologyEvaluator();

	private PointSetDirectionEvaluator createTurnTopologyEvaluator() {
		return PointSetDirectionEvaluator.create();
	}

	@Test
	public void testHorizontalPasses() {
		assertTilesInDirectionAreCorrect(Direction.HORIZONTAL);
	}
	

	@Test
	public void testVerticalPasses() {
		assertTilesInDirectionAreCorrect(Direction.VERTICAL);
	}

	private void assertTilesInDirectionAreCorrect(Direction d) {
		Collection<IPoint> collection = Util.makeList(ORIGIN, d, 5);
		Direction result = eval.evaluate(collection);
		assertEquals(d, result);
	}

	@Test
	public void testHorizontalGapTilesFail() {
		assertTilesInDirectionWithGapFail(Direction.HORIZONTAL);
	}

	@Test
	public void testVerticalGapTilesFail() {
		assertTilesInDirectionWithGapFail(Direction.VERTICAL);
	}
	
	private void assertTilesInDirectionWithGapFail(Direction d) {
		List<IPoint> list = Util.makeList(ORIGIN, d, 5);
		list.remove(d.increasingFunction().apply(list.get(0)));
		Direction result = eval.evaluate(list);
		assertEquals(result, Direction.INDETERMINATE);
	}
	
	@Test
	public void testDiagonalFails() {
		ImmutableSet<? extends IPoint> points = ImmutableSet.of(new Point(0,0), new Point(1,1));
		Direction actual = eval.evaluate(points);
		assertEquals(Direction.INDETERMINATE, actual);
	}
	

}
