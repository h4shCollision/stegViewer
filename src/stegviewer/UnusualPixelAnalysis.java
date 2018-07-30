package stegviewer;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.LinkedList;
import java.util.Optional;

// detects pixels that are different in areas that are supposed to be uniform
public class UnusualPixelAnalysis {

	private static final double IGNORE = 3;

	public static void analyze() {
		buildAndShowAlert().ifPresent(res -> {
			int[][][] pixels = ImageUtils.getPixelsMatrix(ImageManager.getCurrent().getBufferedImage());
			int[][][] newPix = new int[pixels.length][pixels[0].length][pixels[0][0].length];
			int[] white = {255, 255, 255};
			int w = pixels.length;
			int h = pixels[0].length;
			int r = res.getRadius();
			if (r <= 0) {
				System.out.println("Invalid Radius");
				return;
			}
			if (r + r >= w || r + r >= h) {
				System.out.println("Image too small/radius too large");
				return;
			}
			for (int i = 0; i < w; i++) {
				for (int j = 0; j < h; j++) {
					LinkedList<int[]> nearby = new LinkedList<>();
					for (int k = -r; k < r + 1; k++) {
						for (int l = -r; l < r + 1; l++) {
							if (valid(i + k, j + l, w, h)) {
								nearby.add(pixels[i + k][j + l]);
							}
						}
					}
					if (weird(nearby, pixels[i][j], res.threshold)) {
						if (res.useOriginalColor) {
							newPix[i][j] = pixels[i][j];
						} else {
							newPix[i][j] = white;
						}
					}
				}
			}
			ImageManager.addImage(ImageUtils.constructImage(ImageManager.getCurrent().getBufferedImage(), newPix));
		});
	}

	private static Optional<DialogResult> buildAndShowAlert() {
		Dialog<DialogResult> alert = new Dialog<>();
		alert.setTitle("Pixel analysis");
		alert.setContentText("Detect pixels that are unusual");

		VBox vRoot = new VBox();
		vRoot.setSpacing(5);

		HBox radiusBox = new HBox();
		radiusBox.getChildren().add(new Label("Radius: "));
		TextField radiusText = new TextField("2");
		radiusBox.getChildren().add(radiusText);
		vRoot.getChildren().add(radiusBox);

		HBox thresholdBox = new HBox();
		thresholdBox.getChildren().add(new Label("threshold: "));
		TextField thresholdText = new TextField("1.0");
		thresholdBox.getChildren().add(thresholdText);
		vRoot.getChildren().add(thresholdBox);

		CheckBox origColor = new CheckBox("Use original colours");
		vRoot.getChildren().add(origColor);

		alert.setResultConverter(buttonType -> {
			if (buttonType != ButtonType.APPLY) {
				return null;
			}
			try {
				int radius = Integer.parseInt(radiusText.getText());
				double threashold = Double.parseDouble(thresholdText.getText());
				boolean keepColor = origColor.isSelected();
				return new DialogResult(keepColor, radius, threashold);
			} catch (Exception e) {
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

	private static boolean valid(int x, int y, int w, int h) {
		return 0 <= x && x < w && 0 <= y && y < h;
	}

	private static boolean weird(LinkedList<int[]> nearby, int[] curr, double threshold) {
		double[] average = new double[3];
		for (int[] pix : nearby) {
			for (int i = 0; i < 3; i++) {
				average[i] += pix[i];
			}
		}
		for (int i = 0; i < 3; i++) {
			average[i] /= nearby.size();
		}
		double x = 0;
		for (int[] pix : nearby) {
			x += Math.pow(diff(h(pix), average), 2);
		}
		x = Math.sqrt(x / nearby.size());
		//if (x >= IGNORE) {
		//	return false;
		//}
		if (Math.abs(x) < 2 * Double.MIN_VALUE) {
			return false;
		}
		return diff(h(curr), average) / x >= threshold;
	}

	private static double diff(double[] a, double[] b) {
		double sum = 0;
		for (int i = 0; i < Math.min(a.length, b.length); i++) {
			sum += Math.abs(a[i] - b[i]);
		}
		return sum;
	}

	private static double[] h(int[] x) {
		double[] y = new double[x.length];
		for (int i = 0; i < x.length; i++) {
			y[i] = (double) x[i];
		}
		return y;
	}

	private static class DialogResult {
		private boolean useOriginalColor;
		private int radius;
		private double threshold;

		public DialogResult(boolean useOriginalColor, int radius, double threshold) {
			this.useOriginalColor = useOriginalColor;
			this.radius = radius;
			this.threshold = threshold;
		}

		public boolean isUseOriginalColor() {
			return useOriginalColor;
		}

		public int getRadius() {
			return radius;
		}

		public double getThreshold() {
			return threshold;
		}
	}
}
