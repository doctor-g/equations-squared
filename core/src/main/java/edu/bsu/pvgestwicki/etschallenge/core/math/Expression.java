package edu.bsu.pvgestwicki.etschallenge.core.math;

public interface Expression extends Evaluatable {

	public interface Visitor {
		void visit(int number);

		void visit(Operation operation);

		void visit(Variable variable);
		
		void visit(UnaryOperation unaryOperation);

		public abstract class Adapter implements Visitor {
			@Override
			public void visit(int number) {
			}

			@Override
			public void visit(Operation operator) {
			}

			@Override
			public void visit(Variable variable) {
			}

			@Override
			public void visit(UnaryOperation unaryOperation) {
			}
		}
	}

	void accept(Visitor visitor);
}
