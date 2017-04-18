package edu.bsu.pvgestwicki.etschallenge.core.achieve;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import tripleplay.util.Logger;
import edu.bsu.pvgestwicki.etschallenge.core.math.Equation;
import edu.bsu.pvgestwicki.etschallenge.core.math.EquationParser;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.Success;

public class BadgeTest {

	private AchievementChecker checker;
	private Success success;
	
	@Before
	public void setUp() {
		Logger.setImpl(null);
	}

	private Equation parse(String equationString) {
		return new EquationParser().parse(equationString);
	}

	@Test
	public void testLevel4IsEarnedByUsingOperatorsOnBothSidesOfEquals() {
		checkingFor(Badge.LEVEL_4);
		playAndEarnBadge("1+2=0+3");
	}

	private void setupSuccess(String eqAsString) {
		Equation eq = parse(eqAsString);
		success = new Success(eq);
	}

	@Test
	public void testLevel3IsEarnedByPlayingEquationsWithOperatorsOnEitherSideOfEquals() {
		checkingFor(Badge.LEVEL_3);
		playWithoutEarningBadge("1+1=2");
		playAndEarnBadge("4=3+1");
	}

	private void checkingFor(Badge b) {
		checker = b.checker();
	}

	private void playWithoutEarningBadge(String eqString) {
		setupSuccess(eqString);
		boolean earned = checker.check(success);
		assertFalse(earned);
	}

	private void playAndEarnBadge(String eqString) {
		setupSuccess(eqString);
		boolean earned = checker.check(success);
		assertTrue(earned);
	}

	@Test
	public void testBivariateIsEarnedByPlayingEquationWithTwoVariables() {
		checkingFor(Badge.BIVARIATE);
		playAndEarnBadge("A+B=2");
	}

	@Test
	public void testEqualsEqualsEqualsIsEarnedByPlayingEquationWithTwoEqualsSigns() {
		checkingFor(Badge.EQUALS_EQUALS_EQUALS);
		playAndEarnBadge("1=1=1");
	}

	@Test
	public void testLevel2IsEarnedByReusingAVariable() {
		checkingFor(Badge.LEVEL_2_VAR_REUSE);
		playWithoutEarningBadge("A=2");
		playAndEarnBadge("A=2");
	}
	
	@Test
	public void testUnaryIsEarnedByUsingAUnaryOp() {
		checkingFor(Badge.UNARY);
		playAndEarnBadge("-5=0-5");
	}
	
	@Test
	public void testUnaryIsEarnedByUsingAUnaryOpOnRHS() {
		checkingFor(Badge.UNARY);
		playAndEarnBadge("0-5=-5");
	}
	
	@Test
	public void testUnaryIsEarnedByPositivization() {
		checkingFor(Badge.UNARY);
		playAndEarnBadge("+2=2");
	}
}
