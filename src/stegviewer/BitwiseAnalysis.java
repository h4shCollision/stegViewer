package stegviewer;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BitwiseAnalysis {
	public enum Operation {
		AND, OR, XOR
	}

	public static void analyze() {
		Optional<DialogResult> result = buildAndShowAlert();
		result.ifPresent(dialogResult -> {
			List<BufferedImage> images = ImageManager.getBufferedImages(dialogResult.getIndices());
			if (images.size() <= 1) {
				Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
				infoAlert.setTitle("Insufficient photos for bitwise analysis");
				infoAlert.show();
				return;
			}
			BufferedImage image = merge(images, dialogResult.getOperation());
			ImageManager.addImage(image);
		});
	}

	private static Optional<DialogResult> buildAndShowAlert() {
		Dialog<DialogResult> alert = new Dialog<>();
		alert.setTitle("Bitwise analysis");
		alert.setContentText("Create a new image by bitwise analysis");

		HBox hRoot = new HBox();
		VBox vRoot = new VBox();
		vRoot.setSpacing(5);

		ToggleGroup group = new ToggleGroup();
		boolean selected = true;
		List<RadioButton> operationList = new LinkedList<>();
		for (Operation op : Operation.values()) {
			RadioButton button = new RadioButton(op.toString());
			button.setToggleGroup(group);
			button.setSelected(selected);
			selected = false;
			hRoot.getChildren().add(button);
			operationList.add(button);
		}
		vRoot.getChildren().add(hRoot);

		List<CheckBox> imageList = ImageManager.getNames().stream().map(s -> new CheckBox(s)).collect(Collectors
				.toList());
		vRoot.getChildren().addAll(imageList);

		alert.setResultConverter(buttonType -> {
			if (buttonType == ButtonType.APPLY) {
				DialogResult res = null;
				List<Integer> indices = new LinkedList<>();
				for (int i = 0; i < imageList.size(); ++i) {
					if (imageList.get(i).isSelected())
						indices.add(i);
				}
				for (RadioButton rb : operationList) {
					if (rb.isSelected()) {
						res = new DialogResult(Operation.valueOf(rb.getText()), indices);
						break;
					}
				}
				return res;
			} else {
				return null;
			}
		});

		GridPane.setHgrow(vRoot, Priority.ALWAYS);
		GridPane.setVgrow(vRoot, Priority.ALWAYS);
		GridPane expContent = new GridPane();
		expContent.add(vRoot, 0, 0);

		alert.getDialogPane().setContent(expContent);
		alert.getDialogPane().getButtonTypes().addAll(ButtonType.APPLY, ButtonType.CANCEL);

		return alert.showAndWait();
	}

	private static BufferedImage merge(List<BufferedImage> images, Operation operation) {
		int w = images.get(0).getWidth();
		int h = images.get(0).getHeight();
		int[][][] pixels = ImageUtils.getPixelsMatrix(images.get(0));
		for (int i = 1; i < images.size(); ++i) {
			BufferedImage image = images.get(i);
			WritableRaster raster = image.getRaster();
			if (w == image.getWidth() && h == image.getHeight()) {
				for (int j = 0; j < w; ++j) {
					for (int k = 0; k < h; ++k) {
						int[] pixel = raster.getPixel(j, k, (int[]) null);
						for (int l = 0; l < pixel.length; ++l) {
							pixels[j][k][l] = operation(pixels[j][k][l], pixel[l], operation);
						}
					}
				}
			} else {
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Incompatible images");
				alert.setHeaderText("Cannot do bitwise analysis on images with different sizes");
				alert.show();
				return null;
			}
		}
		return ImageUtils.constructImage(images.get(0), pixels); //random image for color model and stuff
	}

	private static int operation(int a, int b, Operation operation) {
		a = a & 0xFFFFFF;
		b = b & 0xFFFFFF; //ignore a
		switch (operation) {
			case OR:
				return a | b;
			case AND:
				return a & b;
			case XOR:
				return a ^ b;
			default:
				System.out.println("AAAHHHHHHHHHH");
				return 0;
		}
	}

	public static class DialogResult {
		private Operation operation;
		private List<Integer> indices;

		public DialogResult(Operation operation, List<Integer> indices) {
			this.operation = operation;
			this.indices = indices;
		}

		public Operation getOperation() {
			return operation;
		}

		public List<Integer> getIndices() {
			return indices;
		}
	}
}
