package nutella.pendvid;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

public class Util {

	public static String frameFilename(String dir, int num) {
		return dir + File.separator + "f" + num + ".jpg";
	}

	public static String getFilename() {
		JFileChooser jfc = new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int res = jfc.showOpenDialog(null);
		if(res == JFileChooser.CANCEL_OPTION) {
			throw new RuntimeException("must choose file");
		}
		return jfc.getSelectedFile().getAbsolutePath();
	}

	public static PrintStream getPrintStream(String fname) {
		try {
			return new PrintStream(new FileOutputStream(fname));
		} catch(IOException e) {
			throw new RuntimeException("could not open output stream");
		}
	}

	public static BufferedImage getImg(String dir, int num) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(frameFilename(dir, num)));
		} catch(IOException e) {
			throw new RuntimeException("could not load image file");
		}
		return img;
	}

	public static int maxFrameNum(String dir) {
		int i = 0;
		while(true) {
			if(!(new File(frameFilename(dir, i))).exists()) {
				return i;
			}
			i++;
		}
	}

	/* utility: */
	public static String prettyPoint(Point p) {
		return Util.prettyPoint(p.x, p.y);
	}

	public static String prettyPoint(int x, int y) {
		return "(" + x + "," + y + ")";
	}

	public static String prettyPoint(PointD p) {
		return Util.prettyPoint(p.x, p.y);
	}

	public static String prettyPoint(double x, double y) {
		return "(" + x + "," + y + ")";
	}

	public static AsyncLoader loadImgsAsync(
			final ConcurrentLinkedQueue<BufferedImage> imgq,
			final String dir,
			final int numFrames) {
		AsyncLoader loader = new AsyncLoader(numFrames, imgq, dir);
		loader.start();
		return loader;
	}

}
