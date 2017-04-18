package edu.bsu.pvgestwicki.etschallenge.core.pos;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import pythagoras.i.IPoint;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import edu.bsu.pvgestwicki.etschallenge.core.model.TileArea;
import edu.bsu.pvgestwicki.etschallenge.core.util.TopologicalComparator;

public final class PlayedStringDeterminer {

	public static PlayedStringDeterminer on(TileArea board) {
		return new PlayedStringDeterminer(board);
	}

	private final TileArea board;
	private List<IPoint> playedSequence;

	private PlayedStringDeterminer(TileArea board) {
		this.board = checkNotNull(board);
	}

	public List<PlayedString> sequence(Collection<? extends IPoint> playedSequence) {
		if (playedSequence.size() == 0) {
			return emptyResult();
		} else {
			this.playedSequence = Lists.newArrayList(playedSequence);
			Collections.sort(this.playedSequence,
					TopologicalComparator.instance());
			return processNonemptySequence();
		}
	}

	private List<PlayedString> emptyResult() {
		Collection<IPoint> empty = ImmutableSet.of();
		PlayedString played = new PlayedStringImpl("", empty);
		return Lists.newArrayList(played);
	}

	private List<PlayedString> processNonemptySequence() {
		Set<PlayedString> result = Sets.newHashSet();
		for (IPoint p : playedSequence) {
			Set<PlayedString> partial = findHorizontalAndVerticalStringsIncidentOn(p);
			result.addAll(partial);
		}
		return Lists.newArrayList(result);
	}

	private Set<PlayedString> findHorizontalAndVerticalStringsIncidentOn(
			IPoint p) {
		Set<PlayedString> result = Sets.newHashSet();
		Set<PlayedString> horizontal = findStringsIncidentOn(p,
				Direction.HORIZONTAL);
		result.addAll(horizontal);
		Set<PlayedString> vertical = findStringsIncidentOn(p,
				Direction.VERTICAL);
		result.addAll(vertical);
		return result;
	}

	private Set<PlayedString> findStringsIncidentOn(IPoint p, Direction d) {
		Set<IPoint> lesserNeighbors = findNeighbors(p, d.increasingFunction());
		Set<IPoint> greaterNeighbors = findNeighbors(p, d.decreasingFunction());
		if (lesserNeighbors.isEmpty() && greaterNeighbors.isEmpty())
			return ImmutableSet.of();
		else {
			return constructPlayedStringFrom(p, lesserNeighbors,
					greaterNeighbors);
		}
	}

	private Set<IPoint> findNeighbors(IPoint p, DirectionFunction d) {
		Set<IPoint> set = Sets.newHashSet();
		IPoint next = d.apply(p);
		while (boardOrPlayedSequenceContains(next)) {
			set.add(next);
			next = d.apply(next);
		}
		return set;
	}

	private boolean boardOrPlayedSequenceContains(IPoint p) {
		return board.contains(p)
				&& (board.at(p).isOccupied() || playedSequence.contains(p));
	}

	private Set<PlayedString> constructPlayedStringFrom(IPoint p,
			Set<IPoint> neighbors, Set<IPoint> moreNeighbors) {
		Set<IPoint> all = Sets.newHashSet();
		all.addAll(neighbors);
		all.addAll(moreNeighbors);
		all.add(p);
		PlayedString playedString = makePlayedStringOf(all);
		return ImmutableSet.of(playedString);
	}

	private PlayedString makePlayedStringOf(Set<IPoint> all) {
		List<IPoint> list = Lists.newArrayList(all);
		Collections.sort(list, TopologicalComparator.instance());
		StringBuffer sb = new StringBuffer();
		for (IPoint p : list) {
			String partialString = board.at(p).tile().value();
			sb.append(partialString);
		}
		return new PlayedStringImpl(sb.toString(), list);
	}

}

final class PlayedStringImpl implements PlayedString {

	private final String s;
	private final ImmutableSet<IPoint> points;

	public PlayedStringImpl(String s, Collection<IPoint> points) {
		this.s = checkNotNull(s);
		this.points = ImmutableSet.copyOf(checkNotNull(points));
	}

	@Override
	public String string() {
		return s;
	}

	@Override
	public Collection<IPoint> points() {
		return points;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PlayedStringImpl) {
			PlayedStringImpl other = (PlayedStringImpl) obj;
			return Objects.equal(this.s, other.s)
					&& Objects.equal(this.points, other.points);
		} else
			return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(s, points);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("s", s).add("points", points)
				.toString();
	}

}
