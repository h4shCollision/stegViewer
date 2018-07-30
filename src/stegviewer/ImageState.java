package stegviewer;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageState {

	private String name;
	private File file;
	private BufferedImage bufferedImage;

	public ImageState(File imageFile) {
		this(imageFile, imageFile.getName());
	}

	public ImageState(File imageFile, String name) {
		this.name = name;
		this.file = imageFile;
		try {
			this.bufferedImage = ImageIO.read(imageFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ImageState(BufferedImage image, String name) {
		this.name = name;
		this.bufferedImage = image;
	}

	public String getName() {
		return name;
	}

	public Image getImage() {
		return SwingFXUtils.toFXImage(bufferedImage, null);
	}

	public File getFile() {
		return file;
	}

	public BufferedImage getBufferedImage() {
		return bufferedImage;
	}

	public ImageState setFile(File file) {
		this.file = file;
		return this;
	}
}
