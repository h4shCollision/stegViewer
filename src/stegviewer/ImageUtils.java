package stegviewer;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

public class ImageUtils {

	public static BufferedImage constructImage(BufferedImage bi, int[][][] pixels) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		for (int j = 0; j < pixels.length; ++j) {
			for (int k = 0; k < pixels[0].length; ++k) {
				raster.setPixel(j, k, pixels[j][k]);
			}
		}
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	public static int[][][] getPixelsMatrix(BufferedImage image) {
		WritableRaster raster = image.getRaster();
		int[][][] pixels = new int[image.getWidth()][image.getHeight()][];
		for (int j = 0; j < pixels.length; ++j) {
			for (int k = 0; k < pixels[0].length; ++k) {
				pixels[j][k] = raster.getPixel(j, k, (int[]) null);
			}
		}
		return pixels;
	}

}
