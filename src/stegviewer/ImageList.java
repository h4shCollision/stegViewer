package stegviewer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class ImageList extends ListView<ImageState> implements Callback<ListView<ImageState>, ListCell<ImageState>> {

	public ImageList() {
		this.setCellFactory(this);
		getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ImageState>() {
			public void changed(ObservableValue<? extends ImageState> observable, ImageState oldValue, ImageState newValue) {
				if (newValue != oldValue) {
					ImageManager.setCurrentState(newValue);
				}
			}
		});
	}

	public ListCell<ImageState> call(ListView<ImageState> param) {
		return new ImageCell();
	}
}
