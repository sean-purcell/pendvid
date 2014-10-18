package nutella.pendvid;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

public class Util {

	public static String frameFilename(String dir, int num) {
		return dir + File.separator + "f" + num + ".jpg";
	}

	public static String getOpenFilename(int mode) {
		JFileChooser jfc = new JFileChooser();
		jfc.setFileSelectionMode(mode);
		int res = jfc.showOpenDialog(null);
		if(res == JFileChooser.CANCEL_OPTION) {
			throw new RuntimeException("must choose file");
		}
		return jfc.getSelectedFile().getAbsolutePath();
	}

	public static String getWriteFilename(int mode) {
		JFileChooser jfc = new JFileChooser();
		jfc.setFileSelectionMode(mode);
		int res = jfc.showSaveDialog(null);
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
			final String dir,
			final int numFrames) {
		AsyncLoader loader = new AsyncLoader(numFrames, dir);
		loader.start();
		return loader;
	}

	public static List<Point> convert(boolean[][] a) {
		List<Point> list = new ArrayList<Point>();
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				if (a[i][j]) {
					list.add(new Point(i, j));
				}
			}
		}
		return list;
	}
	
	public static BufferedImage crop(BufferedImage img, Point[] bounds) {
		int dx = bounds[1].x - bounds[0].x;
		int dy = bounds[1].y - bounds[0].y;
		return img.getSubimage(bounds[0].x, bounds[0].y, dx, dy);
	}
}
