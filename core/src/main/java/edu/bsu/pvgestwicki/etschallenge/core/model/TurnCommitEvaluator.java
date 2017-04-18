package edu.bsu.pvgestwicki.etschallenge.core.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Set;

import pythagoras.i.IPoint;
import react.Value;

import com.google.common.collect.Sets;

import edu.bsu.pvgestwicki.etschallenge.core.math.Environment;
import edu.bsu.pvgestwicki.etschallenge.core.math.Equation;
import edu.bsu.pvgestwicki.etschallenge.core.math.EquationParser;
import edu.bsu.pvgestwicki.etschallenge.core.math.Expression;
import edu.bsu.pvgestwicki.etschallenge.core.math.Expression.Visitor;
import edu.bsu.pvgestwicki.etschallenge.core.math.NotAnEquationException;
import edu.bsu.pvgestwicki.etschallenge.core.math.ParseException;
import edu.bsu.pvgestwicki.etschallenge.core.math.UnboundVariableException;
import edu.bsu.pvgestwicki.etschallenge.core.math.Variable;
import edu.bsu.pvgestwicki.etschallenge.core.pos.CommonAxisPredicate;
import edu.bsu.pvgestwicki.etschallenge.core.pos.PlayedString;
import edu.bsu.pvgestwicki.etschallenge.core.pos.PlayedStringDeterminer;
import edu.bsu.pvgestwicki.etschallenge.core.pos.PointOutliner;

public final class TurnCommitEvaluator {

	public static Builder on(TileArea board) {
		return new Builder(board);
	}

	public final static class Builder {
		private final TileArea board;

		private Builder(TileArea board) {
			this.board = checkNotNull(board);
		}

		public TurnCommitEvaluator in(Environment env) {
			return new TurnCommitEvaluator(board, env);
		}
	}

	private final TileArea board;
	private final Environment env;
	private Set<IPoint> playedPoints;

	private TurnCommitEvaluator(TileArea board, Environment env) {
		this.board = checkNotNull(board);
		this.env = checkNotNull(env);
	}

	public CommitResult evaluateCommit(Turn turn) {
		playedPoints = turn.placements().keySet();

		if (!CommonAxisPredicate.instance().apply(playedPoints)) {
			return new CommitResult.IllegalMove();
		}

		List<PlayedString> playedStrings = PlayedStringDeterminer.on(board)
				.sequence(playedPoints);
		if (playedStrings.size() == 0) {
			return new CommitResult.IllegalMove();
		}

		if (!playedPointsTouchExistingTilesIfThereAreAny()) {
			return new CommitResult.NonintersectingMove();
		}

		CommitResultAccumulator accumulator = CommitResultAccumulator.create();
		for (int i = 0; i < playedStrings.size(); i++) {
			CommitResult result = eval(playedStrings.get(i).string());
			accumulator.add(result);
		}
		return accumulator.result();
	}

	private CommitResult eval(String equationAttempt) {
		try {
			Equation equation = new EquationParser().parse(equationAttempt);

			Variable unbound = scanForUnboundVariables(equation);
			if (unbound != null)
				return new CommitResult.UnboundVariable(unbound);

			boolean balanced = equation.isTrue(env);
			if (balanced) {
				return new CommitResult.Success(equation);
			} else {
				return new CommitResult.UnbalancedEquation(equation);
			}
		} catch (NotAnEquationException naee) {
			return new CommitResult.NotAnEquation();
		} catch (ParseException parseException) {
			return new CommitResult.MalformedEquation();
		} catch (ArithmeticException divideByZero) {
			return new CommitResult.DivideByZero();
		} catch (UnboundVariableException unbound) {
			throw new IllegalStateException();// should have been handled above
		}
	}

	private Variable scanForUnboundVariables(Equation equation) {
		final Value<Variable> unbound = Value.create(null);
		for (Expression e : equation.expressions()) {
			e.accept(new Visitor.Adapter() {
				@Override
				public void visit(Variable variable) {
					if (!env.hasBindingFor(variable))
						unbound.update(variable);
				}
			});
			if (unbound.get() != null)
				return unbound.get();
		}
		return null;
	}

	private boolean playedPointsTouchExistingTilesIfThereAreAny() {
		if (boardContainsTilesBesidesThePlayedPoints()) {
			return playedPointsTouchExistingTiles();
		} else {
			return true;
		}
	}

	private boolean boardContainsTilesBesidesThePlayedPoints() {
		Set<IPoint> occupiedPoints = board.occupiedPoints();
		Set<IPoint> difference = Sets.difference(occupiedPoints, playedPoints);
		return !difference.isEmpty();
	}

	private boolean playedPointsTouchExistingTiles() {
		Set<IPoint> outlineOfPlayedPoints = PointOutliner.withSize(board.width(), board.height()).apply(playedPoints);
		
		Set<IPoint> difference = Sets.difference(outlineOfPlayedPoints,
				board.occupiedPoints());
		
		return difference.size() < outlineOfPlayedPoints.size();
	}

}
