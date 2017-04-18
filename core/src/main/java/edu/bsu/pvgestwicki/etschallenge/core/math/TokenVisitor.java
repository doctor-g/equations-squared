package edu.bsu.pvgestwicki.etschallenge.core.math;

public interface TokenVisitor<T> {

	T visit(IntegerToken integerToken);
	T visit(SymbolToken symbolToken);
	T visit(VariableToken variableToken);
	
	public static abstract class Abstract<T> implements TokenVisitor<T> {

		@Override
		public T visit(IntegerToken integerToken) {
			return null;
		}

		@Override
		public T visit(SymbolToken symbolToken) {
			return null;
		}

		@Override
		public T visit(VariableToken variableToken) {
			return null;
		}
	}

	
}
