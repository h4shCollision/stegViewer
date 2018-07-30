package stegviewer;

import javafx.scene.image.ImageView;

public class MainImageView extends ImageView {

	ImageState state;

	public MainImageView() {
		super();
	}

	public void setState(ImageState state) {
		this.state = state;
		setImage(state.getImage());
		//TODO: scale, location
	}

}
