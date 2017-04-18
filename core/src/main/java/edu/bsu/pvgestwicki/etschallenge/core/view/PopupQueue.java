package edu.bsu.pvgestwicki.etschallenge.core.view;

import static com.google.common.base.Preconditions.checkNotNull;
import static playn.core.PlayN.currentTime;

import java.util.List;

import playn.core.GroupLayer;

import com.google.common.collect.Lists;

import edu.bsu.pvgestwicki.etschallenge.core.GameConfiguration;
import edu.bsu.pvgestwicki.etschallenge.core.util.LayerEliminator;
import edu.bsu.pvgestwicki.etschallenge.core.util.Time;

public final class PopupQueue {

	public static PopupQueue createFor(GroupLayer parent) {
		return new PopupQueue(parent);
	}

	private List<PopupText> queue = Lists.newLinkedList();
	private final GroupLayer parent;
	private double lastPopup = 0;
	private final double minMSBetweenPopups = 900;

	private PopupQueue(GroupLayer parent) {
		this.parent = checkNotNull(parent);
	}

	public PopupQueue enqueue(PopupText popup) {
		checkNotNull(popup);
		queue.add(popup);
		return this;
	}

	public void update(float deltaMS) {
		if (itIsTimeToPopup()) {
			popupNext();
		}
	}

	private boolean itIsTimeToPopup() {
		return queue.size() > 0
				&& currentTime() - lastPopup > minMSBetweenPopups;
	}

	private void popupNext() {
		PopupText popup = queue.remove(0);
		parent.add(popup.layer());
		animate(popup);
		popup.playSound();
		lastPopup = currentTime();
	}

	private void animate(PopupText popup) {
		GameConfiguration
				.instance()
				.animator()
				.tweenTranslation(popup.layer())
				.from(325, 320)
				.to(325, 300)
				.in(Time.seconds(0.9f))
				.easeOut()
				.then()
				.tweenAlpha(popup.layer())
				.to(0)
				.in(Time.seconds(0.3f))
				.easeOut()
				.then()
				.action(LayerEliminator.layer(popup.layer()).fromParent(parent));
	}
}
