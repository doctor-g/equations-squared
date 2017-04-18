package edu.bsu.pvgestwicki.etschallenge.core.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import playn.core.PlayN;
import react.Slot;

import com.google.common.collect.ImmutableMap;

import edu.bsu.pvgestwicki.etschallenge.core.achieve.Achievement;
import edu.bsu.pvgestwicki.etschallenge.core.achieve.Badge;
import edu.bsu.pvgestwicki.etschallenge.core.achieve.Demerit;
import edu.bsu.pvgestwicki.etschallenge.core.math.Equation;
import edu.bsu.pvgestwicki.etschallenge.core.math.Expression;
import edu.bsu.pvgestwicki.etschallenge.core.math.Operation;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.Success;
import edu.bsu.pvgestwicki.etschallenge.core.util.DigitCounter;
import edu.bsu.pvgestwicki.etschallenge.core.util.VariableExtractor;

public final class OperatorAndAchievementScoringStrategy {

	private static final Map<Operation, Integer> OPERATION_BASE_SCORE = ImmutableMap
			.of(Operation.ADD, 5,//
					Operation.SUBTRACT, 6,//
					Operation.MULTIPLY, 7,//
					Operation.DIVIDE, 8);

	private static final Map<Badge, Integer> BADGE_SCORES = new ImmutableMap.Builder<Badge, Integer>()
			.put(Badge.LEVEL_2_VAR_REUSE, 5)//
			.put(Badge.LEVEL_3, 5)//
			.put(Badge.LEVEL_4, 5)//
			.put(Badge.BIVARIATE, 5)//
			.put(Badge.EQUALS_EQUALS_EQUALS, 5)//
			.put(Badge.UNARY, 5)//
			.build();

	static {
		for (Badge b : Badge.values()) {
			if (!BADGE_SCORES.containsKey(b))
				PlayN.log().warn("No score for badge: " + b);
		}
	}

	private final class ExpressionScorer extends
			Expression.Visitor.Adapter {

		private int score = 0;
		private Expression expression;
		
		private ExpressionScorer(Expression e) {
			this.expression = checkNotNull(e);
		}

		@Override
		public void visit(Operation operation) {
			int operationScore = determineOperationScoreOf(operation);
			if (score == 0) {
				score = operationScore;
			} else {
				score *= operationScore;
			}
		}

		private int determineOperationScoreOf(Operation operation) {
			int raw = OPERATION_BASE_SCORE.get(operation);
			if (!equationHasVariables) {
				raw += longestLiteralInExpression();
			}
			return raw;
		}

		private int longestLiteralInExpression() {
			LongestLiteralProcessor proc = new LongestLiteralProcessor();
			expression.accept(proc);
			return proc.max;
		}

		public int score() {
			return score;
		}

	}
	
	private static final class LongestLiteralProcessor extends Expression.Visitor.Adapter {
		private static final DigitCounter digitCounter = DigitCounter.instance();
		int max = 0;
		
		@Override
		public void visit(int number) {
			int digits = digitCounter.countDigitsIn(number);
			this.max = Math.max(max, digits);
		}
	}

	private boolean equationHasVariables = false;

	private final CommitResult.Visitor<Void> successScorer = new CommitResult.Visitor.Adapter<Void>(
			null) {
		@Override
		public Void visit(Success success, Object... args) {
			int points = 0;
			for (Equation eq : success.equations()) {
				determineIfCurrentEquationHasVariables(eq);
				for (Expression e : eq.expressions()) {
					points += scoreExpression(e);
				}
			}
			addToScore(points, "+" + points);
			return null;
		}

		private void determineIfCurrentEquationHasVariables(Equation eq) {
			equationHasVariables = VariableExtractor.from(eq).size() > 0;
		}

		private int scoreExpression(Expression e) {
			ExpressionScorer scorer = new ExpressionScorer(e);
			e.accept(scorer);
			return scorer.score();
		}
	};

	private final Achievement.Visitor<Void> achievementScorer = new Achievement.Visitor<Void>() {

		@Override
		public Void visit(Badge badge) {
			Integer points = BADGE_SCORES.get(badge);
			if (points == null)
				PlayN.log().warn("Missing score mapping for " + badge);
			else {
				addToScore(points, badge.friendlyName() + "  +" + points);
			}
			return null;
		}

		@Override
		public Void visit(Demerit demerit) {
			return null;
		}

	};

	public static OperatorAndAchievementScoringStrategy forGame(GameModel game) {
		return new OperatorAndAchievementScoringStrategy(game);
	}

	private final GameModel game;

	private OperatorAndAchievementScoringStrategy(GameModel game) {
		this.game = checkNotNull(game);
		watch(game);
	}

	private void watch(GameModel gameModel) {
		gameModel.onCommit().connect(new Slot<CommitResult>() {
			@Override
			public void onEmit(CommitResult result) {
				result.accept(successScorer);
			}
		});
		gameModel.achievementTracker().achievementAdded()
				.connect(new Slot<Achievement>() {
					@Override
					public void onEmit(Achievement event) {
						event.accept(achievementScorer);
					}
				});
	}

	private void addToScore(int points, String mesg) {
		game.score().add(points, mesg);
	}
}
