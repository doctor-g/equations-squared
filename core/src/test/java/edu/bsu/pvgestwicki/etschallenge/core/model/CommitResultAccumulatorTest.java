package edu.bsu.pvgestwicki.etschallenge.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import edu.bsu.pvgestwicki.etschallenge.core.math.Equation;
import edu.bsu.pvgestwicki.etschallenge.core.math.EquationParser;
import edu.bsu.pvgestwicki.etschallenge.core.math.Variable;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.MalformedEquation;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.Success;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.UnboundVariable;

public class CommitResultAccumulatorTest {

	private CommitResultAccumulator accumulator = CommitResultAccumulator
			.create();

	@Test
	public void testSuccessIsSuccess() {
		accumulator.add(new CommitResult.Success(anyEquation()));
		assertResultIsA(Success.class);
	}

	@Test
	public void testMalformedIsMalformed() {
		accumulator.add(new CommitResult.MalformedEquation());
		assertResultIsA(MalformedEquation.class);
	}

	@Test
	public void testSuccessAndSuccessIsSuccess() {
		for (int i = 0; i < 2; i++)
			accumulator.add(new CommitResult.Success(anyEquation()));
		assertResultIsA(Success.class);
	}

	@Test
	public void testSuccessAndMalformedIsMalformed() {
		accumulator.add(new CommitResult.Success(anyEquation()));
		accumulator.add(new CommitResult.MalformedEquation());
		assertResultIsA(MalformedEquation.class);
	}

	@Test
	public void testMalformedAndSuccessIsMalformed() {
		accumulator.add(new CommitResult.MalformedEquation());
		accumulator.add(new CommitResult.Success(anyEquation()));
		assertResultIsA(MalformedEquation.class);
	}

	@Test
	public void testUnboundIsLowerPriorityThanMalformedSoUnboundThenMalformedIsMalformed() {
		accumulator.add(new CommitResult.UnboundVariable(mock(Variable.class)));
		accumulator.add(new CommitResult.MalformedEquation());
		assertResultIsA(MalformedEquation.class);
	}

	@Test
	public void testUnboundIsLowerPriorityThanMalformedSoMalformedThenUnboundIsMalformed() {
		accumulator.add(new CommitResult.MalformedEquation());
		accumulator.add(new CommitResult.UnboundVariable(mock(Variable.class)));
		assertResultIsA(MalformedEquation.class);
	}

	@Test
	public void testUnboundAndSuccessIsUnbound() {
		accumulator.add(new CommitResult.UnboundVariable(mock(Variable.class)));
		accumulator.add(new CommitResult.Success(anyEquation()));
		assertResultIsA(UnboundVariable.class);
	}

	@Test
	public void testSuccessAndUnboundIsUnbound() {
		accumulator.add(new CommitResult.Success(anyEquation()));
		accumulator.add(new CommitResult.UnboundVariable(mock(Variable.class)));
		assertResultIsA(UnboundVariable.class);
	}
	
	@Test
	public void testTwoSuccessesBothAreCarriedInResult() {
		Equation[] equations = { parse("1+1=2"), parse("2+2=4") };
		for (Equation eq : equations)
			accumulator.add(successful(eq));
		assertAccumulatedResultContainsAllOf(equations);
	}

	private void assertAccumulatedResultContainsAllOf(Equation[] equations) {
		CommitResult.Success success = (CommitResult.Success)accumulator.result();
		for (Equation eq : equations) {
			assertTrue(success.equations().contains(eq));
		}
	}

	private Equation parse(String equationString) {
		return new EquationParser().parse(equationString);
	}
	
	private CommitResult successful(Equation equation) {
		return new CommitResult.Success(equation);
	}

	private void assertResultIsA(Class<? extends CommitResult> type) {
		assertEquals(type, accumulator.result().getClass());
	}

	private Equation anyEquation() {
		return new EquationParser().parse("1+1=2");
	}
}
