package edu.bsu.pvgestwicki.etschallenge.core.pos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import pythagoras.i.IPoint;
import pythagoras.i.Point;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import edu.bsu.pvgestwicki.etschallenge.core.model.TileArea;
import edu.bsu.pvgestwicki.etschallenge.core.model.TileModel;

public class PlayedStringDeterminerTest {

	private TileArea board = TileArea.create(11, 11);

	@Test
	public void testEmptyPlaySetYieldsSingleEmptyString() {
		Set<IPoint> empty = ImmutableSet.of();
		List<PlayedString> played = PlayedStringDeterminer.on(board).sequence(
				empty);
		assertEquals(1, played.size());
		assertEquals("", played.iterator().next().string());
	}

	@Test
	public void testHorizontalSequenceOnEmptyBoardReturnsSameSequence() {
		assertSequenceOnEmptyBoardIsDetected(Direction.HORIZONTAL);
	}

	@Test
	public void testVerticalSequenceOnEmptyBoardReturnsSameSequence() {
		assertSequenceOnEmptyBoardIsDetected(Direction.VERTICAL);
	}

	private void assertSequenceOnEmptyBoardIsDetected(Direction d) {
		List<IPoint> sequence = Util.makeList(new Point(0, 0), d, 5);
		putOnBoard(sequence);
		List<PlayedString> played = PlayedStringDeterminer.on(board).sequence(
				sequence);
		assertHasOneSequenceThatMatchesStringAndPointSequence("55555", sequence, played);
	}

	private void putOnBoard(Iterable<? extends IPoint> points) {
		for (IPoint p : points) {
			board.at(p).place(mockTile());
		}
	}

	private void assertHasOneSequenceThatMatchesStringAndPointSequence(String expectedString,
			Iterable<? extends IPoint> expectedSequence,
			List<PlayedString> actual) {
		assertHasOneSequenceThatMatches(expectedString, actual);
		assertSameContents(expectedSequence, actual.get(0).points());
	}

	private void assertSameContents(Iterable<? extends IPoint> expected,
			Iterable<IPoint> actual) {
		ImmutableSet<IPoint> one = ImmutableSet.copyOf(expected);
		ImmutableSet<IPoint> two = ImmutableSet.copyOf(actual);
		assertTrue(
				"This symmetric difference should be empty: "
						+ Sets.symmetricDifference(one, two),//
				Sets.symmetricDifference(one, two).isEmpty());
	}

	private TileModel mockTile() {
		TileModel tile = mock(TileModel.class);
		when(tile.value()).thenReturn("5");
		return tile;
	}

	@Test
	public void testHorizontalSequenceWithHoleFilledByBoardTile() {
		Set<Point> sequence = ImmutableSet.of(new Point(0, 0), new Point(0, 2));
		putOnBoard(sequence);
		board.at(0, 1).place(mockTile());
		List<PlayedString> played = PlayedStringDeterminer.on(board).sequence(
				sequence);
		assertHasOneSequenceThatMatches("555", played);
	}
	
	private void assertHasOneSequenceThatMatches(String expectedString, List<PlayedString> actual) {
		assertEquals(1, actual.size());
		assertEquals(expectedString, actual.get(0).string());
	}
	
	@Test
	public void testTilePlacedInCornerGivesBothResults() {
		putOnBoard(ImmutableSet.of(new Point(0,1), new Point(0,2)));
		putOnBoard(ImmutableSet.of(new Point(1,0)));
		
		Set<Point> singleTilePlay = ImmutableSet.of(new Point(0,0));
		putOnBoard(singleTilePlay);
		
		List<PlayedString> played = PlayedStringDeterminer.on(board).sequence(
				singleTilePlay);
		
		assertHasTwoSequences("55", "555", played);
	}

	private void assertHasTwoSequences(String expected1, String expected2,
			List<PlayedString> actual) {
		assertEquals(2, actual.size());
		Set<String> strings = ImmutableSet.of(actual.get(0).string(), actual.get(1).string());
		assertTrue("Contains " + expected1, strings.contains(expected1));
		assertTrue("Contains " + expected2, strings.contains(expected2));
		
	}
	
	

}
