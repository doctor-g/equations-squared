package edu.bsu.pvgestwicki.etschallenge.core.achieve;

import static com.google.common.base.Preconditions.checkNotNull;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.Visitor;

final class VisitorBasedChecker implements AchievementChecker {

	private Achievement achievement;
	private Visitor<Boolean> visitor;

	public VisitorBasedChecker(Achievement achievement,
			CommitResult.Visitor<Boolean> visitor) {
		this.achievement = checkNotNull(achievement);
		this.visitor = checkNotNull(visitor);
	}

	@Override
	public boolean check(CommitResult commitResult) {
		checkNotNull(commitResult);
		return commitResult.accept(visitor);
	}

	@Override
	public Achievement achievement() {
		return achievement;
	}

}
