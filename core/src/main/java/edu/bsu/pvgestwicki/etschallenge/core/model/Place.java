package edu.bsu.pvgestwicki.etschallenge.core.model;

import com.google.common.base.Objects;

import pythagoras.i.IPoint;
import pythagoras.i.Point;

public interface Place {

	public final class Equals implements Place {

		@Override
		public <T> T accept(Visitor<T> visitor, Object... args) {
			return visitor.visit(this,args);
		}

		@Override
		public String toString() {
			return Objects.toStringHelper(this).toString();
		}
	}

	public final class Board implements Place {
		private final Point position;

		public Board(int i, int j) {
			this.position = new Point(i, j);
		}

		public int i() {
			return position.x;
		}

		public int j() {
			return position.y;
		}

		public IPoint point() {
			return position;
		}
		
		@Override
		public <T> T accept(Visitor<T> visitor, Object... args) {
			return visitor.visit(this,args);
		}

		@Override
		public String toString() {
			return Objects.toStringHelper(this).add("position",position).toString();
		}
		
		
	}

	public final class Tray implements Place {
		private final int i;

		public Tray(int i) {
			this.i = i;
		}

		public int position() {
			return i;
		}
		
		@Override
		public <T> T accept(Visitor<T> visitor, Object... args) {
			return visitor.visit(this,args);
		}

		@Override
		public String toString() {
			return Objects.toStringHelper(this).add("i", i).toString();
		}
		
		
	}

	public interface Visitor<T> {
		public T visit(Equals eq, Object... args);
		public T visit(Board board, Object... args);
		public T visit(Tray tray, Object... args);
	}
	
	public <T> T accept(Visitor<T> visitor, Object... args);
}
