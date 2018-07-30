package stegviewer;

// example: https://en.wikipedia.org/wiki/Steganography#/media/File:Steganography_recovered.png

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.Optional;

public class LeastSignificantBits {
	private static final boolean COMPLEMENT = true;
	private static final int[] lazy = {1, 3, 7, 15, 31, 63, 127};

	public static void analyze() {
		TextInputDialog dialog = new TextInputDialog("2");
		dialog.setTitle("How many least significant bits");

		Optional<String> result = dialog.showAndWait();
		if (!result.isPresent()) {
			return;
		}
		int nBits = Integer.parseInt(result.get());
		if (nBits <= 0 || nBits >= 8) {
			return;
		}
		int[][][] pixels = ImageUtils.getPixelsMatrix(ImageManager.getCurrent().getBufferedImage());
		int w = pixels.length;
		int h = pixels[0].length;
		int rgb = pixels[0][0].length;
		int tmp = lazy[nBits - 1];
		for (int i = 0; i < w; ++i) {
			for (int j = 0; j < h; ++j) {
				for (int k = 0; k < rgb; ++k) {
					pixels[i][j][k] = (pixels[i][j][k] ^ tmp) << (8 - nBits);
					if (COMPLEMENT) {
						pixels[i][j][k] = 255 - pixels[i][j][k]; //example needs this
					}
				}
			}
		}
		ImageManager.addImage(ImageUtils.constructImage(ImageManager.getCurrent().getBufferedImage(), pixels));
	}

	public static void encode() {
		buildAndShowAlert().ifPresent((res) -> {
			if (res == null || res.getNbits() <= 0 || res.getNbits() >= 8) {
				System.out.println("Invalid input");
				return;
			}

			int mainMask = lazy[8 - res.getNbits() - 1] << res.getNbits();
			int hiddenMask = lazy[res.getNbits() - 1];
			int[][][] main = ImageUtils.getPixelsMatrix(ImageManager.getBufferedImage(res.getMainIdx()));
			int[][][] hidden = ImageUtils.getPixelsMatrix(ImageManager.getBufferedImage(res.getHiddenIdx()));
			if (main.length != hidden.length || main[0].length != hidden[0].length || main[0][0].length !=
					hidden[0][0].length) {
				System.out.println("Two images must have the same dimension");
				return;
			}
			System.out.println(hiddenMask + " " + mainMask + " " + res.getNbits());
			int w = main.length;
			int h = main[0].length;
			int rgb = main[0][0].length;
			int[][][] encrypted = new int[w][h][rgb];
			for (int i = 0; i < w; ++i) {
				for (int j = 0; j < h; ++j) {
					for (int k = 0; k < rgb; ++k) {
						if (COMPLEMENT) {
							encrypted[i][j][k] = 255 - encrypted[i][j][k];
						}
						encrypted[i][j][k] = ((hidden[i][j][k] >> (8 - res.getNbits())) & hiddenMask) + (main[i][j][k] &
								mainMask);

					}
				}
			}
			ImageManager.addImage(ImageUtils.constructImage(ImageManager.getBufferedImage(res.getMainIdx()), encrypted));
		});
	}

	private static Optional<DialogResult> buildAndShowAlert() {
		Dialog<DialogResult> alert = new Dialog<>();
		alert.setTitle("Bitwise analysis");
		alert.setContentText("Create a new image by bitwise analysis");

		VBox vRoot = new VBox();
		vRoot.setSpacing(5);

		HBox bitsBox = new HBox();
		bitsBox.getChildren().add(new Label("Bits: "));
		TextField bits = new TextField();
		bitsBox.getChildren().add(bits);

		HBox imageMain = new HBox();
		imageMain.getChildren().add(new Label("Main image: "));
		ObservableList<ImageState> images = FXCollections.observableArrayList(ImageManager.getImages());
		ComboBox<ImageState> selectMain = new ComboBox<>(images);
		selectMain.setConverter(new StringConverter<ImageState>() {
			@Override
			public String toString(ImageState object) {
				return object.getName();
			}

			@Override
			public ImageState fromString(String string) {
				return null;
			}
		});
		imageMain.getChildren().add(selectMain);

		HBox imageHidden = new HBox();
		imageHidden.getChildren().add(new Label("Hidden image: "));
		ComboBox<ImageState> selectHidden = new ComboBox<>(images);
		selectHidden.setConverter(new StringConverter<ImageState>() {
			@Override
			public String toString(ImageState object) {
				return object.getName();
			}

			@Override
			public ImageState fromString(String string) {
				return null;
			}
		});
		imageHidden.getChildren().add(selectHidden);

		alert.setResultConverter(buttonType -> {
			if (buttonType == ButtonType.APPLY) {
				LeastSignificantBits.DialogResult res = new DialogResult();
				try {
					res.setNbits(Integer.parseInt(bits.getText()));
				} catch (Exception e) {
					return null;
				}
				res.setMainIdx(selectMain.getSelectionModel().getSelectedIndex());
				res.setHiddenIdx(selectHidden.getSelectionModel().getSelectedIndex());
				return res;
			} else {
				return null;
			}
		});
		vRoot.getChildren().add(bitsBox);
		vRoot.getChildren().add(imageMain);
		vRoot.getChildren().add(imageHidden);

		GridPane.setHgrow(vRoot, Priority.ALWAYS);
		GridPane.setVgrow(vRoot, Priority.ALWAYS);
		GridPane expContent = new GridPane();
		expContent.add(vRoot, 0, 0);

		alert.getDialogPane().setContent(expContent);
		alert.getDialogPane().getButtonTypes().addAll(ButtonType.APPLY, ButtonType.CANCEL);

		return alert.showAndWait();
	}

	private static class DialogResult {
		private int nbits;
		private int hiddenIdx;
		private int mainIdx;

		public int getNbits() {
			return nbits;
		}

		public DialogResult setNbits(int nbits) {
			this.nbits = nbits;
			return this;
		}

		public int getHiddenIdx() {
			return hiddenIdx;
		}

		public DialogResult setHiddenIdx(int hiddenIdx) {
			this.hiddenIdx = hiddenIdx;
			return this;
		}

		public int getMainIdx() {
			return mainIdx;
		}

		public DialogResult setMainIdx(int mainIdx) {
			this.mainIdx = mainIdx;
			return this;
		}
	}
}
