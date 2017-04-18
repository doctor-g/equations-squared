package edu.bsu.pvgestwicki.etschallenge.core;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

public class ListEqualityTest {

	@Test
	public void testTwoArrayListsWithSameElementsAreEqual() {
		List<String> list1 = Lists.newArrayList("foo", "bar");
		List<String> list2 = Lists.newArrayList("foo", "bar");
		assertTrue(list1.equals(list2));
	}
}
