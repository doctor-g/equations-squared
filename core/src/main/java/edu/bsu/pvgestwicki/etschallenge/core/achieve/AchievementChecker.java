package edu.bsu.pvgestwicki.etschallenge.core.achieve;

import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult;

public interface AchievementChecker {

	public boolean check(CommitResult commitResult);
	
	public Achievement achievement();
}
