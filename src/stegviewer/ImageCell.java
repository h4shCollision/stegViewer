package stegviewer;

import javafx.scene.control.ListCell;

public class ImageCell extends ListCell<ImageState> {
	@Override
	protected void updateItem(ImageState item, boolean empty) {
		super.updateItem(item, empty);
		if (item != null) {
			setText(item.getName());
		}
	}
}
