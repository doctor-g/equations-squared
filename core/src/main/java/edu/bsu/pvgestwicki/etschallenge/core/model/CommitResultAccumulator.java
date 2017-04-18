package edu.bsu.pvgestwicki.etschallenge.core.model;

import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.DivideByZero;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.IllegalMove;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.MalformedEquation;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.NonintersectingMove;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.NotAnEquation;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.Success;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.UnbalancedEquation;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.UnboundVariable;

public final class CommitResultAccumulator {

	public static CommitResultAccumulator create() {
		return new CommitResultAccumulator();
	}

	private static final CommitResult.Visitor<CommitResult> resultCombiner = new CommitResult.Visitor<CommitResult>() {

		@Override
		public CommitResult visit(Success success, Object... args) {
			CommitResult other = fromArgs(args);
			if (other.accept(isSuccess))
				return success.append( ((CommitResult.Success)other).equations() );
			else
				return other;
		}

		private CommitResult fromArgs(Object[] args) {
			return (CommitResult) args[0];
		}

		@Override
		public CommitResult visit(UnbalancedEquation unbalanced, Object... args) {
			return unbalanced;
		}

		@Override
		public CommitResult visit(MalformedEquation malformed, Object... args) {
			return malformed;
		}

		@Override
		public CommitResult visit(NotAnEquation notAnEquation, Object... args) {
			return notAnEquation;
		}

		@Override
		public CommitResult visit(DivideByZero divideByZero, Object... args) {
			return divideByZero;
		}

		@Override
		public CommitResult visit(UnboundVariable unboundVariable,
				Object... args) {
			CommitResult other = fromArgs(args);
			if (other.accept(isSuccess))
				return unboundVariable;
			else
				return other;
		}

		@Override
		public CommitResult visit(IllegalMove illegalMove, Object... args) {
			return illegalMove;
		}

		@Override
		public CommitResult visit(NonintersectingMove nonintersectingMove,
				Object... args) {
			return nonintersectingMove;
		}

	};

	private static final CommitResult.Visitor<Boolean> isSuccess = new CommitResult.Visitor.Adapter<Boolean>(
			false) {
		@Override
		public Boolean visit(Success success, Object... args) {
			return true;
		}
	};

	private CommitResult resultSoFar;

	private CommitResultAccumulator() {
	}

	public void add(CommitResult commitResult) {
		if (resultSoFar == null)
			resultSoFar = commitResult;
		else
			resultSoFar = resultSoFar.accept(resultCombiner, commitResult);
	}

	public CommitResult result() {
		return resultSoFar;
	}
}
