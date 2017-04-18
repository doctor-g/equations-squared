package edu.bsu.pvgestwicki.etschallenge.core.achieve;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.bsu.pvgestwicki.etschallenge.core.math.Environment;
import edu.bsu.pvgestwicki.etschallenge.core.math.EnvironmentBuilder;
import edu.bsu.pvgestwicki.etschallenge.core.math.Equation;
import edu.bsu.pvgestwicki.etschallenge.core.math.EquationParser;
import edu.bsu.pvgestwicki.etschallenge.core.math.VariableBuilder;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.Success;

public class DemeritTest {

	private AchievementChecker checker;

	@Test
	public void testBoringIsEarnedByPlayingSimpleEquation() {
		checkingFor(Demerit.BORING);
		Equation eq = parse("1=1");
		Success success = new Success(eq);
		assertTrue(checker.check(success));
	}

	private Equation parse(String s) {
		return new EquationParser().parse(s);
	}

	private void checkingFor(Demerit boring) {
		this.checker = boring.checker();
	}

	@Test
	public void testDivideByZero() {
		checkingFor(Demerit.DIVIDE_BY_ZERO);
		assertTrue(checker.check(new CommitResult.DivideByZero()));
	}

	@Test
	public void testUnbalanced() {
		checkingFor(Demerit.UNBALANCED_WITHOUT_VARS);
		Equation eq = parse("1=0");
		assertTrue(checker.check(new CommitResult.UnbalancedEquation(eq)));
	}

	@Test
	public void testUnbalancedWithVars() {
		checkingFor(Demerit.UNBALANCED_WITH_VARS);
		Equation eq = parse("A=0");
		assertTrue(checker.check(new CommitResult.UnbalancedEquation(eq)));
	}
	
	@Test
	public void testMalformed() {
		checkingFor(Demerit.MALFORMED);
		assertTrue(checker.check(new CommitResult.MalformedEquation()));
	}
	
	@Test
	public void testMalformedOnNonEquationResult() {
		checkingFor(Demerit.MALFORMED);
		assertTrue(checker.check(new CommitResult.NotAnEquation()));
	}
	
	@Test
	public void testExpressionDoesNotFireSimpleIdentity() {
		AchievementChecker checker = Demerit.BORING.checker();
		CommitResult.Success success = new CommitResult.Success(new EquationParser().parse("Y=9*-1"));
		Environment env = EnvironmentBuilder.create();
		env.bind(VariableBuilder.create("Y"), -9);
		assertFalse(checker.check(success));
	}
}
