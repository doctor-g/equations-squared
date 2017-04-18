package edu.bsu.pvgestwicki.etschallenge.core.math;

import com.google.common.base.Objects;

public final class IntegerToken implements Token {

	public static IntegerToken create(String integerAsString) {
		int intValue = Integer.parseInt(integerAsString);
		return new IntegerToken(intValue);
	}
	
	public static IntegerToken create(int i) {
		return new IntegerToken(i);
	}
	
	private final int value;
	
	private IntegerToken(int value) {
		this.value=value;
	}

	@Override
	public <T> T accept(TokenVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public int value() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IntegerToken) {
			IntegerToken other = (IntegerToken)obj;
			return this.value==other.value;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(value);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("value", value).toString();
	}
	
	
	
}
