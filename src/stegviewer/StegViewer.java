package stegviewer;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class StegViewer extends Application {
	private static final String TITLE = "StegViewer";

	public static void main(String args[]) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane pane = new BorderPane();
		Scene scene = new Scene(pane, 300, 300, Color.WHITE);
		setupMenuBar(primaryStage, pane);
		setupMainImage(pane);
		setupList(pane);
		randomSettings(primaryStage);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void setupMainImage(BorderPane pane) {
		MainImageView imageView = new MainImageView();
		StackPane imageHolder = new StackPane(imageView);
		ScrollPane sp = new ScrollPane();
		sp.setContent(imageHolder);
		imageHolder.minWidthProperty().bind(Bindings.createDoubleBinding(() ->
				sp.getViewportBounds().getWidth(), sp.viewportBoundsProperty()));
		imageHolder.minHeightProperty().bind(Bindings.createDoubleBinding(() ->
				sp.getViewportBounds().getHeight(), sp.viewportBoundsProperty()));
		pane.setCenter(sp);
		ImageManager.setView(imageView);
	}

	private void setupList(BorderPane p) {
		ImageList list = new ImageList();
		ImageManager.setImages(list.getItems());
		p.setLeft(list);
	}

	private void setupMenuBar(Stage primaryStage, BorderPane pane) {
		MenuBar bar = new MenuBar();
		initializeFile(bar, primaryStage);
		initializeAnalysis(bar, primaryStage);
		initializeEncrypt(bar, primaryStage);
		bar.useSystemMenuBarProperty().set(true);
		bar.prefWidthProperty().bind(primaryStage.widthProperty());
		pane.setTop(bar);
	}

	private void initializeEncrypt(MenuBar bar, Stage primaryStage) {
		MenuItem lsb = new MenuItem("Least Significant bits");
		lsb.setOnAction(event -> LeastSignificantBits.encode());

		Menu encryptMenu = new Menu("Encrypt");
		encryptMenu.getItems().add(lsb);
		encryptMenu.setOnShowing(value -> {
			lsb.setDisable(ImageManager.getNames().isEmpty());
		});
		bar.getMenus().add(encryptMenu);
	}

	private void initializeFile(MenuBar bar, final Stage primaryStage) {
		MenuItem fileOpen = new MenuItem("Open");
		fileOpen.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN));
		fileOpen.setOnAction(event -> FileUtils.openImage(primaryStage));

		MenuItem fileSave = new MenuItem("Save");
		fileSave.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
		fileSave.setOnAction(event -> FileUtils.saveImage(primaryStage, true));

		MenuItem fileSaveAs = new MenuItem("Save As");
		fileSaveAs.setOnAction(event -> FileUtils.saveImage(primaryStage, false));

		Menu fileMenu = new Menu("File");
		fileMenu.getItems().add(fileOpen);
		fileMenu.getItems().add(fileSave);
		fileMenu.getItems().add(fileSaveAs);
		fileMenu.setOnShowing(value -> {
			boolean b = ImageManager.getCurrent() == null || ImageManager.getCurrent().getBufferedImage() == null;
			fileSave.setDisable(b);
			fileSaveAs.setDisable(b);
		});
		bar.getMenus().add(fileMenu);
	}

	private void initializeAnalysis(MenuBar bar, final Stage primaryStage) {
		MenuItem bitWise = new MenuItem("Bitwise");
		bitWise.setOnAction(event -> BitwiseAnalysis.analyze());

		MenuItem lsb = new MenuItem("Least Significant Bits");
		lsb.setOnAction(event -> LeastSignificantBits.analyze());

		MenuItem unusual = new MenuItem("Unusual");
		unusual.setOnAction(event -> UnusualPixelAnalysis.analyze());

		Menu analysisMenu = new Menu("Analysis");
		analysisMenu.getItems().add(bitWise);
		analysisMenu.getItems().add(lsb);
		analysisMenu.getItems().add(unusual);
		analysisMenu.setOnShowing(event -> {
			lsb.setDisable(ImageManager.getCurrent() == null || ImageManager
					.getCurrent().getBufferedImage() == null);
			unusual.setDisable(ImageManager.getCurrent() == null || ImageManager
					.getCurrent().getBufferedImage() == null);
			bitWise.setDisable(ImageManager.getNames().isEmpty());
		});
		bar.getMenus().add(analysisMenu);
	}

	private void randomSettings(Stage primaryStage) {
		primaryStage.setMaximized(true);
		primaryStage.setTitle(TITLE);
	}
}
