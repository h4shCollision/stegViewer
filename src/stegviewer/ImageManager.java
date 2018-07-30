package stegviewer;

import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.gif.GIFImageReaderSpi;
import javafx.collections.ObservableList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ImageManager {

	private static List<ImageState> items;
	private static MainImageView view;
	private static ImageState current;
	private static final String DEFAULT_NAME = "new";
	private static final String GIF_EXT = "gif";

	public static void setView(MainImageView view) {
		ImageManager.view = view;
	}

	public static void addFiles(List<File> files) {
		if (files == null) {
			return;
		}
		for (File file : files) {
			try {
				if (GIF_EXT.equals(getFileExtension(file))) {
					ImageReader ir = new GIFImageReader(new GIFImageReaderSpi());
					ir.setInput(ImageIO.createImageInputStream(file));
					for (int i = 0; i < ir.getNumImages(true); i++) {
						ImageManager.addImage(ir.read(i), file.getName());
					}
					continue;
				}
			} catch (Exception e) {

			}
			String name = computeName(file.getName());
			items.add(new ImageState(file, name));
		}
	}

	public static void addImage(BufferedImage image) {
		addImage(image, DEFAULT_NAME);
	}

	public static void addImage(BufferedImage image, String name) {
		items.add(new ImageState(image, computeName(name)));
	}

	public static void setImages(ObservableList<ImageState> items) {
		ImageManager.items = items;
	}

	public static void setCurrentState(ImageState imageState) {
		current = imageState;
		if (imageState == null) {
			clearAll();
		}
		view.setImage(imageState.getImage());
	}

	public static ImageState getCurrent() {
		return current;
	}

	public static List<String> getNames() {
		return items.stream().map(imageState -> imageState.getName()).collect(Collectors.toList());
	}

	public static List<BufferedImage> getBufferedImages(List<Integer> indices) {
		return indices.stream().map(i -> items.get(i).getBufferedImage()).collect(Collectors.toList());
	}

	public static BufferedImage getBufferedImage(int index) {
		return items.get(index).getBufferedImage();
	}

	public static List<ImageState> getImages() {
		return items;
	}

	private static String computeName(String name) {
		Set<String> names = new HashSet<>(getNames());
		if (!names.contains(name)) {
			return name;
		}
		int i = 1;
		while (names.contains(name + '#' + i)) {
			i += 1;
		}
		return name + '#' + i;
	}

	private static void clearAll() {
		view.setImage(null);
	}

	private static String getFileExtension(File file) {
		String fileName = file.getName();
		if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		} else {
			return null;
		}
	}
}