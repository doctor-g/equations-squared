package edu.bsu.pvgestwicki.etschallenge.core.util;

import java.util.List;

import com.google.common.collect.ImmutableList;

public final class TileImageSheetConstants {

	private static final Character[] CHAR_ARRAY = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', '+', '-',
			SpecialChar.MULTIPLICATION_SIGN.asCharacter(),
			SpecialChar.OBELUS.asCharacter(), '=', 'A', 'B', 'Y', 'Z' };

	public static final List<Character> CHARACTERS = ImmutableList.copyOf(CHAR_ARRAY);

}
