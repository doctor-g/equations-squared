package edu.bsu.pvgestwicki.etschallenge.core.achieve;

import java.util.List;

import edu.bsu.pvgestwicki.etschallenge.core.math.Equation;
import edu.bsu.pvgestwicki.etschallenge.core.math.Expression;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.DivideByZero;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.MalformedEquation;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.NotAnEquation;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.Success;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.UnbalancedEquation;
import edu.bsu.pvgestwicki.etschallenge.core.util.TermCounter;
import edu.bsu.pvgestwicki.etschallenge.core.util.VariableExtractor;

public enum Demerit implements Achievement {
	BORING("Simple Identity", "Single terms on each side of the equals") {

		private final CommitResult.Visitor<Boolean> isSimpleEquation = new CommitResult.Visitor.Adapter<Boolean>(
				false) {
			@Override
			public Boolean visit(Success success, Object... args) {
				for (Equation eq : success.equations()) {
					List<Expression> expressions = eq.expressions();
					if (expressions.size() == 2 //
							&& TermCounter.count(expressions.get(0)) == 1//
							&& TermCounter.count(expressions.get(1)) == 1)
						return true;
				}
				return false;
			}
		};

		@Override
		public AchievementChecker checker() {
			return new VisitorBasedChecker(BORING, isSimpleEquation);
		}

	},

	DIVIDE_BY_ZERO("Divide by Zero", "It cannot be done.") {

		private final CommitResult.Visitor<Boolean> isDivisionByZero = new CommitResult.Visitor.Adapter<Boolean>(
				false) {
			@Override
			public Boolean visit(DivideByZero divideByZero, Object... args) {
				return true;
			}
		};

		@Override
		public AchievementChecker checker() {
			return new VisitorBasedChecker(DIVIDE_BY_ZERO, isDivisionByZero);
		}

	},

	UNBALANCED_WITHOUT_VARS("Unbalanced Equation", "Well-formed but false.") {

		private final CommitResult.Visitor<Boolean> isUnbalancedWithoutUsingVariables = new DefaultRejectingVisitor() {
			@Override
			public Boolean visit(UnbalancedEquation unbalanced, Object... args) {
				return VariableExtractor.from(unbalanced.equation()).size() == 0;
			}
		};

		@Override
		public AchievementChecker checker() {
			return new VisitorBasedChecker(UNBALANCED_WITHOUT_VARS,
					isUnbalancedWithoutUsingVariables);
		}

	},

	UNBALANCED_WITH_VARS("Unbalanced Variable Equation",
			"Well-formed but false.") {

		private final CommitResult.Visitor<Boolean> isUnbalancedWithVariables = new DefaultRejectingVisitor() {
			@Override
			public Boolean visit(UnbalancedEquation unbalanced, Object... args) {
				return VariableExtractor.from(unbalanced.equation()).size() > 0;
			}
		};

		@Override
		public AchievementChecker checker() {
			return new VisitorBasedChecker(UNBALANCED_WITH_VARS,
					isUnbalancedWithVariables);
		}

	},

	MALFORMED("Not an Equation", "Tried to play a non-equation.") {

		private final CommitResult.Visitor<Boolean> isMalformed = new DefaultRejectingVisitor() {
			@Override
			public Boolean visit(MalformedEquation malformed, Object... args) {
				return true;
			}

			@Override
			public Boolean visit(NotAnEquation notAnEquation, Object... args) {
				return true;
			}
		};

		@Override
		public AchievementChecker checker() {
			return new VisitorBasedChecker(MALFORMED, isMalformed);
		}

	};

	private final String name;
	private final String desc;

	private Demerit(String name, String desc) {
		this.name = name;
		this.desc = desc;
	}

	@Override
	public String description() {
		return desc;
	}

	@Override
	public String friendlyName() {
		return name;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visit(this);
	}

}

final class CommitResultTypeCheck extends CommitResult.Visitor.Adapter<Boolean> {
	public CommitResultTypeCheck() {
		super(false);
	}

}

abstract class DefaultRejectingVisitor extends
		CommitResult.Visitor.Adapter<Boolean> {

	public DefaultRejectingVisitor() {
		super(false);
	}

}
