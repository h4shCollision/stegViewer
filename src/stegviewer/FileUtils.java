package stegviewer;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class FileUtils {

	private static FileChooser fileChooser;

	public static void openImage(Stage stage) {
		if (fileChooser == null) {
			initFileChooser();
		}
		ImageManager.addFiles(fileChooser.showOpenMultipleDialog(stage));
	}

	public static void saveImage(Stage stage, boolean saveAs) {
		ImageState state = ImageManager.getCurrent();
		if (state == null) {
			return;
		}
		File file = state.getFile();
		if (file == null || saveAs) {
			file = fileChooser.showSaveDialog(stage);
			if (file == null) return;
			state.setFile(file);
		}
		Image image = state.getImage();
		try {
			ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
		} catch (IOException e) {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("Error saving file");
			alert.showAndWait();
		}
	}

	private static void initFileChooser() {
		fileChooser = new FileChooser();
		fileChooser.setTitle("Open Images");
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg",
				"*.gif", "*.jpeg"));
	}

}
