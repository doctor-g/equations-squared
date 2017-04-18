package edu.bsu.pvgestwicki.etschallenge.core.model;

import static com.google.common.base.Preconditions.checkState;

import java.util.List;
import java.util.Map;

import pythagoras.i.IPoint;
import react.Slot;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.bsu.pvgestwicki.etschallenge.core.model.Place.Board;
import edu.bsu.pvgestwicki.etschallenge.core.model.Place.Equals;
import edu.bsu.pvgestwicki.etschallenge.core.model.Place.Tray;
import edu.bsu.pvgestwicki.etschallenge.core.model.Place.Visitor;
import edu.bsu.pvgestwicki.etschallenge.core.util.ExposedForTesting;

public final class Turn {

	public static Turn create() {
		return new Turn();
	}

	@ExposedForTesting
	static Turn createFromTestData(List<Move> moves) {
		Turn t = Turn.create();
		for (Move m : moves) {
			t.handleMove(m);
		}
		return t;
	}

	private List<Move> moves = Lists.newArrayList();
	private Map<IPoint, TileModel> placements = Maps.newHashMap();

	private Turn() {
	}

	public boolean isEmpty() {
		return true;
	}

	public List<Move> moves() {
		return ImmutableList.copyOf(moves);
	}

	public void registerWith(GameModel game) {
		game.onTileMoved().connect(new Slot<Move>() {
			@Override
			public void onEmit(Move move) {
				handleMove(move);
			}
		});
	}

	private void handleMove(Move move) {
		moves.add(move);
		recordAsPlacement(move);
	}

	private void recordAsPlacement(final Move move) {
		boolean handled = move.source().accept(new Visitor<Boolean>() {
			@Override
			public Boolean visit(Equals eq, Object... args) {
				return false;
			}

			@Override
			public Boolean visit(final Board sourceBoard, Object... args) {
				return move.destination().accept(new Visitor<Boolean>() {
					@Override
					public Boolean visit(Equals eq, Object... args) {
						checkState(EqualsTilePredicate.instance().apply(
								move.tile()));
						placements.remove(sourceBoard.point());
						return true;
					}

					@Override
					public Boolean visit(Board board, Object... args) {
						placements.remove(sourceBoard.point());
						placements.put(board.point(), move.tile());
						return true;
					}

					@Override
					public Boolean visit(Tray tray, Object... args) {
						placements.remove(sourceBoard.point());
						return true;
					}
				});
			}

			@Override
			public Boolean visit(Tray tray, Object... args) {
				return false;
			}
		});

		if (!handled) {
			move.destination().accept(new Visitor<Void>() {
				@Override
				public Void visit(Equals eq, Object... args) {
					return null;
				}

				@Override
				public Void visit(Board board, Object... args) {
					placements.put(board.point(), move.tile());
					return null;
				}

				@Override
				public Void visit(Tray tray, Object... args) {
					return null;
				}
			});
		}
	}

	public Map<IPoint, TileModel> placements() {
		return ImmutableMap.copyOf(placements);
	}

}
