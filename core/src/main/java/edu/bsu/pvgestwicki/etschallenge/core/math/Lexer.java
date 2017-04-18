package edu.bsu.pvgestwicki.etschallenge.core.math;

import java.util.List;

import com.google.common.collect.Lists;

public final class Lexer {

	public static Lexer create() {
		return new Lexer();
	}

	private List<Token> result = Lists.newArrayList();
	private char[] array;
	private int index;

	private Lexer() {
	}

	public List<Token> analyze(String string) {
		reset();
		array = string.toCharArray();
		for (; index < array.length; index++) {
			lexNextToken();
		}
		return result;
	}

	private void reset() {
		result.clear();
		index = 0;
		array = null;
	}

	private void lexNextToken() {
		char c = array[index];
		if (Character.isDigit(c))
			lexInteger();
		else if (Character.isLetter(c))
			lexVariable();
		else
			lexSymbol();
	}

	private void lexInteger() {
		String intString = lexIntegerString();
		IntegerToken token = IntegerToken.create(intString);
		result.add(token);
		index--;
	}

	private String lexIntegerString() {
		StringBuffer sb = new StringBuffer();
		for (char c = array[index]; indexIsWithinArray()
				&& currentCharacterIsADigit(); index++) {
			c = array[index];
			sb.append(c);
		}
		return sb.toString();
	}

	private boolean indexIsWithinArray() {
		return index < array.length;
	}

	private boolean currentCharacterIsADigit() {
		return Character.isDigit(array[index]);
	}

	private void lexVariable() {
		char variableCharacter = array[index];
		String variableName = String.valueOf(variableCharacter);
		VariableToken tok = VariableToken.create(variableName);
		result.add(tok);
	}

	private void lexSymbol() {
		SymbolToken tok = SymbolToken.parse(array[index]);
		result.add(tok);
	}

}
