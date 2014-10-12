package nutella.pendvid;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

public class BobClick {
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

	public static void main(String[] args) {
		String dir = null;
		if(args.length == 0) {
			dir = getFilename();
		} else {
			dir = args[0];
		}

		System.out.println(dir);

		GUI gui = new GUI();
		Point[] adjust = gui.getRefLine(getImg(dir, 0));
		System.out.println("Reference vertical line: "
				+ prettyPoint(adjust[0]) + " -- "
				+ prettyPoint(adjust[1]));
	
		int numFrames = maxFrameNum(dir);
		System.out.println("number of frames: " + numFrames);
		List<Point> points = getBobClicks(dir, numFrames, gui);
	}

	public static List<Point> getBobClicks(String dir, int numFrames, GUI gui) {
		List<Point> bobcentres = new ArrayList<Point>(numFrames);
		for(int i = 0; i < numFrames; i++) {
			Point p = gui.getBobClick(getImg(dir, i));
			System.out.println("bob at:\t" + prettyPoint(p));
			bobcentres.add(p);
		}
		return bobcentres;
	}

	/* utility: */
	public static String prettyPoint(Point p) {
		return prettyPoint(p.x, p.y);
	}

	public static String prettyPoint(int x, int y) {
		return "(" + x + "," + y + ")";
	}
}
