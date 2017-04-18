package edu.bsu.pvgestwicki.etschallenge.core.achieve;

import static com.google.common.base.Preconditions.checkState;

import java.util.List;

import react.Signal;
import react.SignalView;
import react.Slot;
import react.Value;
import react.ValueView;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;

import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult;
import edu.bsu.pvgestwicki.etschallenge.core.model.GameModel;

public final class AchievementTracker {

	public static AchievementTracker create() {
		return new AchievementTracker();
	}

	public static DebuggingTrackerBuilder debug() {
		return new DebuggingTrackerBuilder();
	}

	public static final class DebuggingTrackerBuilder {
		private AchievementTracker tracker = AchievementTracker.create();

		private DebuggingTrackerBuilder() {
		}

		public final DebuggingTrackerBuilder add(Achievement achievement) {
			return add(achievement, 1);
		}

		public DebuggingTrackerBuilder add(Achievement achievement, int count) {
			for (int i = 0; i < count; i++)
				tracker.addAchievement(achievement);
			return this;
		}

		public AchievementTracker create() {
			return tracker;
		}
	}

	private final List<AchievementChecker> achievementCheckers = Lists
			.newArrayList();

	private final Multiset<Demerit> demeritBag = HashMultiset.create();
	private final Multiset<Badge> badgeBag = HashMultiset.create();

	private final Value<Integer> demeritCount = Value.create(0);
	private final Value<Integer> badgeCount = Value.create(0);

	private final Signal<Achievement> achievementAdded = Signal.create();

	private final Achievement.Visitor<Void> achievementArchiver = new Achievement.Visitor<Void>() {

		@Override
		public Void visit(Badge badge) {
			badgeBag.add(badge);
			badgeCount.update(badgeBag.size());
			return null;
		}

		@Override
		public Void visit(Demerit demerit) {
			demeritBag.add(demerit);
			demeritCount.update(demeritBag.size());
			return null;
		}

	};

	private AchievementTracker() {
		setupCheckers(Badge.values());
		setupCheckers(Demerit.values());
	}

	private void setupCheckers(Achievement[] values) {
		for (Achievement a : values) {
			achievementCheckers.add(a.checker());
		}
	}

	public void track(GameModel game) {
		game.onCommit().connect(new Slot<CommitResult>() {
			@Override
			public void onEmit(CommitResult event) {
				List<AchievementChecker> spentCheckers = Lists.newArrayList();
				for (AchievementChecker potential : achievementCheckers) {
					checkState(potential!=null);
					if (potential.check(event)) {
						addAchievement(potential.achievement());
						spentCheckers.add(potential);
					}
				}
				for (AchievementChecker checker : spentCheckers) {
					achievementCheckers.remove(checker);
					achievementCheckers.add(checker.achievement().checker());
				}
			}
		});
	}

	private void addAchievement(Achievement achievement) {
		achievement.accept(achievementArchiver);
		achievementAdded.emit(achievement);
	}

	public ImmutableMultiset<Badge> badges() {
		return ImmutableMultiset.copyOf(badgeBag);
	}

	public ImmutableMultiset<Demerit> demerits() {
		return ImmutableMultiset.copyOf(demeritBag);
	}

	public SignalView<Achievement> achievementAdded() {
		return achievementAdded;
	}

	public ValueView<Integer> badgeCount() {
		return badgeCount;
	}

	public ValueView<Integer> demeritCount() {
		return demeritCount;
	}

}
