package edu.bsu.pvgestwicki.etschallenge.core.math;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

public final class VariableToken implements Token {

	public static VariableToken create(String name) {
		return new VariableToken(name);
	}

	private final String name;

	private VariableToken(String name) {
		this.name = checkNotNull(name);
	}

	@Override
	public <T> T accept(TokenVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public String name() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof VariableToken) {
			VariableToken other = (VariableToken) obj;
			return Objects.equal(name, other.name);
		} else
			return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(name);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("name", name).toString();
	}
}
