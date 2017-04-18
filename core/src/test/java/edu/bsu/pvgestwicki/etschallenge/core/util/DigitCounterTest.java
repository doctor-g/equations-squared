package edu.bsu.pvgestwicki.etschallenge.core.util;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.DiscreteDomains;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Range;
import com.google.common.collect.Ranges;

public class DigitCounterTest {

	DigitCounter digitCounter = DigitCounter.instance();

	Map<Range<Integer>, Integer> expected = ImmutableMap.of(//
			Ranges.closed(-99, -10), 2, //
			Ranges.closed(-9, 9), 1, //
			Ranges.closed(10, 99), 2);

	@Test
	public void testZeroThroughNinetyNine() {
		for (Range<Integer> range : expected.keySet()) {
			int expectedValue = expected.get(range);
			assertAllNumbersInRangeAre(range, expectedValue);
		}
	}

	private void assertAllNumbersInRangeAre(Range<Integer> range,
			int expectedValue) {
		for (Integer i : range.asSet(DiscreteDomains.integers())) {
			assertEquals(expectedValue, digitCounter.countDigitsIn(i));
		}
	}

}
