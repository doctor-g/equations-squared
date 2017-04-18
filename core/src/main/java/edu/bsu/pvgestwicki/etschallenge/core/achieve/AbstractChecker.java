package edu.bsu.pvgestwicki.etschallenge.core.achieve;

import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult;

abstract class AbstractChecker implements AchievementChecker {

	private final Achievement achievement;
	private final CommitResult.Visitor<Boolean> visitor;

	AbstractChecker(Achievement achievement) {
		this.achievement = achievement;
		this.visitor = visitor();
	}

	protected abstract CommitResult.Visitor<Boolean> visitor();

	@Override
	public final boolean check(CommitResult commitResult) {
		return commitResult.accept(visitor);
	}

	public final Achievement achievement() {
		return achievement;
	}
}
