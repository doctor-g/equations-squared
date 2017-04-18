package edu.bsu.pvgestwicki.etschallenge.core.achieve;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static edu.bsu.pvgestwicki.etschallenge.core.Log.log;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import edu.bsu.pvgestwicki.etschallenge.core.math.Equation;
import edu.bsu.pvgestwicki.etschallenge.core.math.Expression;
import edu.bsu.pvgestwicki.etschallenge.core.math.UnaryOperation;
import edu.bsu.pvgestwicki.etschallenge.core.math.Variable;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.Success;
import edu.bsu.pvgestwicki.etschallenge.core.util.HasOperationPredicate;
import edu.bsu.pvgestwicki.etschallenge.core.util.VariableExtractor;

public enum Badge implements Achievement {

	LEVEL_2_VAR_REUSE("Variable Reuse", "Reuse a bound variable.") {
		final class ReusedVariables extends SuccessesOnly implements
				CommitResult.Visitor<Boolean> {
			private Set<Variable> usedVariables = Sets.newHashSet();

			// DO NOT REMOVE THIS METHOD.
			// It may look like it does nothing, but in fact, GWT will
			// miscompile this unit without it, or even if the debug message is
			// removed. Long-term, this class should be refactored away from
			// using inner classes in enums.
			private ReusedVariables() {
				log().debug("Created a ReusedVariables object.");
			}

			@Override
			public Boolean visit(Success success, Object... args) {
				for (Equation equation : success.equations()) {
					List<Variable> varsInEquation = VariableExtractor
							.from(equation);
					for (Variable v : varsInEquation) {
						if (usedVariables.contains(v))
							return true;
						else
							usedVariables.add(v);
					}
				}
				return false;
			}
		};

		@Override
		public AchievementChecker checker() {
			return new VisitorBasedChecker(LEVEL_2_VAR_REUSE,
					new ReusedVariables());
		}

	},

	LEVEL_3("Operators on Either Side", "Operators on LHS and RHS.") {
		@Override
		public AchievementChecker checker() {
			return new AbstractChecker(LEVEL_3) {
				@Override
				protected CommitResult.Visitor<Boolean> visitor() {
					return new PlayedOperatorsOnBothSidesInSeparateEquations();
				}
			};
		}
	},

	LEVEL_4("Operators on Both Sides", "Operators on two sides of one equation") {
		private final CommitResult.Visitor<Boolean> playedOperatorsOnBothSides = new SuccessesOnly() {

			@Override
			public Boolean visit(Success success, Object... args) {
				checkNotNull(success);
				checkNotNull(success.equations());
				checkState(success.equations().size() > 0);
				for (Equation eq : success.equations()) {
					List<Expression> expressions = eq.expressions();
					if (earned(expressions)) {
						return true;
					}
				}
				return false;
			}

			private boolean earned(List<Expression> expressions) {
				HasOperationPredicate hasOperator = HasOperationPredicate
						.instance();
				return expressions.size() == 2 //
						&& hasOperator.apply(expressions.get(0))
						&& hasOperator.apply(expressions.get(1));
			}

		};

		@Override
		public AchievementChecker checker() {
			return new VisitorBasedChecker(LEVEL_4, playedOperatorsOnBothSides);
		}
	},

	BIVARIATE("Biviarate", "Use two variables in one equation.") {
		private final CommitResult.Visitor<Boolean> containsMoreThanOneVariable = new SuccessesOnly() {

			@Override
			public Boolean visit(Success success, Object... args) {
				for (Equation eq : success.equations()) {
					int varCount = VariableExtractor.from(eq).size();
					if (varCount > 1)
						return true;
				}
				return false;
			}

		};

		@Override
		public AchievementChecker checker() {
			return new VisitorBasedChecker(BIVARIATE,
					containsMoreThanOneVariable);
		}

	},

	EQUALS_EQUALS_EQUALS("Many Equals", "One equation, two equals signs.") {
		private CommitResult.Visitor<Boolean> hasThreeExpressions = new SuccessesOnly() {
			@Override
			public Boolean visit(Success success, Object... args) {
				for (Equation eq : success.equations()) {
					if (eq.expressions().size() == 3)
						return true;
				}
				return false;
			}
		};

		@Override
		public AchievementChecker checker() {
			return new VisitorBasedChecker(EQUALS_EQUALS_EQUALS,
					hasThreeExpressions);
		}
	},

	UNARY("Unary Operation", "Use unary negative or positive") {
		private final CommitResult.Visitor<Boolean> usesUnaryOperation = new SuccessesOnly() {
			@Override
			public Boolean visit(Success success, Object... args) {
				boolean found = false;
				for (Equation eq : success.equations()) {
					for (Expression ex : eq.expressions()) {
						found = found || hasUnaryOperation(ex);
					}
				}
				return found;
			}

			private boolean hasUnaryOperation(Expression ex) {
				UnaryNegationSeeker seeker = new UnaryNegationSeeker();
				ex.accept(seeker);
				return seeker.found;
			}

			final class UnaryNegationSeeker extends Expression.Visitor.Adapter {
				boolean found = false;

				@Override
				public void visit(UnaryOperation unaryOperation) {
					found = true;
				}

			};

		};

		@Override
		public AchievementChecker checker() {
			return new VisitorBasedChecker(UNARY, usesUnaryOperation);
		}

	};

	private final String name;
	private final String desc;

	private Badge(String name, String desc) {
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

abstract class SuccessesOnly extends CommitResult.Visitor.Adapter<Boolean> {
	public SuccessesOnly() {
		super(false);
	}
}

final class PlayedOperatorsOnBothSidesInSeparateEquations extends SuccessesOnly {
	private List<Expression> expressions;
	private boolean leftPlayed = false;
	private boolean rightPlayed = false;

	@Override
	public Boolean visit(Success success, Object... args) {
		checkState(!leftPlayed || !rightPlayed, "This checker has been spent.");
		boolean found = false;
		for (Equation eq : success.equations()) {
			expressions = eq.expressions();
			checkOperatorOnLeft();
			checkOperatorOnRight();
			found = found || leftAndRightHaveBeenPlayed();
		}
		return found;
	}

	private void checkOperatorOnLeft() {
		if (HasOperationPredicate.on(expressions.get(0))
				&& !HasOperationPredicate.on(expressions.get(1)))
			leftPlayed = true;
	}

	private void checkOperatorOnRight() {
		if (!HasOperationPredicate.on(expressions.get(0))
				&& HasOperationPredicate.on(expressions.get(1)))
			rightPlayed = true;
	}

	private boolean leftAndRightHaveBeenPlayed() {
		return leftPlayed && rightPlayed;
	}
};
