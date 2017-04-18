package edu.bsu.pvgestwicki.etschallenge.core.model;

import com.google.common.base.Predicate;

import edu.bsu.pvgestwicki.etschallenge.core.model.TileModel.EqualsTileModel;
import edu.bsu.pvgestwicki.etschallenge.core.util.MajorTileTypeVisitor;

public final class EqualsTilePredicate implements Predicate<TileModel> {
	
	private static final EqualsTilePredicate SINGLETON = new EqualsTilePredicate();
	
	public static EqualsTilePredicate instance() {
		return SINGLETON;
	}
	
	@Override
	public boolean apply(TileModel input) {
		return input.accept(new MajorTileTypeVisitor<Boolean>() {

			@Override
			public Boolean visit(EqualsTileModel equalsTile, Object... args) {
				return true;
			}

			@Override
			protected Boolean visitNonEqualsTile(TileModel aNonEqualsTileModel,
					Object... args) {
				return false;
			}
			
		});
	}

}
