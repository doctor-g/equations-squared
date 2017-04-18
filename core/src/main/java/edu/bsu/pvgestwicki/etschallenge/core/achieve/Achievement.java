package edu.bsu.pvgestwicki.etschallenge.core.achieve;


public interface Achievement {

	AchievementChecker checker();
	
	interface Visitor<T> {
		T visit(Badge badge);
		T visit(Demerit demerit);
	}
	
	<T> T accept(Visitor<T> visitor);
	
	String friendlyName();
	String description();
}
