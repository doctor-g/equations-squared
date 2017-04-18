package edu.bsu.pvgestwicki.etschallenge.core.view;

import static com.google.common.base.Preconditions.checkNotNull;
import static playn.core.PlayN.graphics;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Mouse;
import playn.core.Mouse.ButtonEvent;
import react.SignalView;
import react.Slot;
import react.UnitSignal;
import react.Value;
import react.ValueView;
import tripleplay.util.MouseInput;
import edu.bsu.pvgestwicki.etschallenge.core.GameConfiguration;
import edu.bsu.pvgestwicki.etschallenge.core.model.TileModel;

public final class TileView {

	private static final TileImageFactory factory = GameConfiguration.instance().tileImageFactory();
	
	public static TileView ofTile(TileModel model) {
		return new TileView(model);
	}

	private final TileModel model;
	private Image selectedImage;
	private Image unselectedImage;
	private Image frozenImage;
	private ImageLayer layer;
	private boolean selected = false;
	private Value<Boolean> movable = Value.create(true);
	private UnitSignal onClick = new UnitSignal();

	private TileView(TileModel model) {
		this.model = checkNotNull(model);
		createImages();
		initLayer();
		initFrozenImageChanger();
	}
	
	private void createImages() {
		char c = model.value().charAt(0);
		selectedImage = factory.selectedImage(c);
		unselectedImage = factory.plainImage(c);
		frozenImage = factory.frozenImage(c);
	}

	
	
	private void initLayer() {
		layer = graphics().createImageLayer(unselectedImage);
		layer.setTranslation(550, 50);
		layer.setSize(50, 50);
	}
	
	private void initFrozenImageChanger() {
		movable.connect(new Slot<Boolean>() {
			@Override
			public void onEmit(Boolean movable) {
				if (!movable) {
					layer().setImage(frozenImage);
				}
			}
		});
	}

	public ImageLayer layer() {
		return layer;
	}

	public TileModel model() {
		return model;
	}

	public ValueView<Boolean> movable() {
		return movable;
	}
	
	public void freeze() {
		movable.update(false);
	}
	
	public void select() {
		layer.setImage(selectedImage);
		selected=true;
	}
	
	public void unselect() {
		layer.setImage(unselectedImage);
		selected=false;
	}
	
	public boolean isSelected(){
		return selected;
	}

	public void register(MouseInput input) {
		input.register(new Mouse.Adapter() {
			@Override
			public void onMouseDown(ButtonEvent event) {
				onClick.emit();
			}
		});
	}
	
	public SignalView<Void> onClick() {
		return onClick;
	}
}
