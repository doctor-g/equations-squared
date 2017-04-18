package edu.bsu.pvgestwicki.etschallenge.core.util;

import edu.bsu.pvgestwicki.etschallenge.core.math.Environment;
import edu.bsu.pvgestwicki.etschallenge.core.math.EnvironmentBuilder;
import edu.bsu.pvgestwicki.etschallenge.core.math.Equation;
import edu.bsu.pvgestwicki.etschallenge.core.math.EquationParser;

public abstract class EquationParsingTest {

	protected Environment env = EnvironmentBuilder.create();
	
	public static Equation parse(String equationString) {
		return new EquationParser().parse(equationString);
	}
	
}
