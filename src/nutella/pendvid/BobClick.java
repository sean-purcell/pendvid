package nutella.pendvid;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

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

	public static void main(String[] args) {
		String dir = null;
		if(args.length == 0) {
			dir = getFilename();
		} else {
			dir = args[0];
		}
		
		String outfile = "out.csv";
		PrintStream out = getPrintStream(outfile);

		System.out.println(dir);

		int numFrames = maxFrameNum(dir);

		GUI gui = new GUI();
		Point[] adjust = gui.getRefLine(
				getImg(dir, 0),
				genLabel("Click on two points along a reference vertical line"));
		System.out.println("Reference vertical line: "
				+ prettyPoint(adjust[0]) + " -- "
				+ prettyPoint(adjust[1]));

		System.out.println("number of frames: " + numFrames);
		Point[][] edgevecs = getEdgeVecs(dir, numFrames, gui);
		System.out.println("edgevec 1: "
				+ prettyPoint(edgevecs[0][0]) + " -- "
				+prettyPoint(edgevecs[0][1]));
		System.out.println("edgevec 2: "
				+ prettyPoint(edgevecs[1][0]) + " -- "
				+prettyPoint(edgevecs[1][1]));
		PointCalc pc = new PointCalc(adjust, edgevecs);
		List<Point> points = getBobClicks(dir, numFrames, gui, pc, out);
		
		out.close();
	}

	public static AsyncLoader loadImgsAsync(
			final ConcurrentLinkedQueue<BufferedImage> imgq,
			final String dir,
			final int numFrames) {
		AsyncLoader loader = new AsyncLoader(numFrames, imgq, dir);
		loader.start();
		return loader;
	}
	
	public static Point[][] getEdgeVecs(final String dir, final int numFrames, final GUI gui) {
		List<Component> instr = genLabel(
			"Define the line for the pendulum swing at the two edges.  Press next to advance to the next image.");
		JButton button = new JButton("next");
		final ConcurrentLinkedQueue<BufferedImage> imgq = new ConcurrentLinkedQueue<BufferedImage>();
		final AsyncLoader asyncLoader = loadImgsAsync(imgq, dir, numFrames);
		button.addActionListener(new ActionListener() {
			private int i = 0;

			@Override
			public void actionPerformed(ActionEvent e1) {
				if(i + 1 >= numFrames)
					return;
				i++;
				gui.setImg(imgq.remove());
				asyncLoader.interrupt();
			}
		});
		instr.add(button);
		Point[][] edgevecs = gui.getEdgeVecs(getImg(dir, 0), instr);
		asyncLoader.done();
		return edgevecs;
	}
	
	public static List<Point> getBobClicks(String dir, int numFrames, GUI gui, PointCalc pc, PrintStream out) {
		List<Point> bobcentres = new ArrayList<Point>(numFrames);
		final ConcurrentLinkedQueue<BufferedImage> imgq = new ConcurrentLinkedQueue<BufferedImage>();
		final AsyncLoader asyncLoader = loadImgsAsync(imgq, dir, numFrames);
		asyncLoader.interrupt();
		for(int i = 0; i < numFrames; i++) {
			while(imgq.size() == 0) {
				try{
					Thread.sleep(50);
				} catch(InterruptedException e) {
				}
			}
			Point p = gui.getBobClick(imgq.remove(), genLabel("Click on bob centre"));
			asyncLoader.interrupt();
			System.out.println("bob at:\t" + prettyPoint(p));
			bobcentres.add(p);
			ClickData click = pc.compClick(p);
			System.out.println("data: " + click);
			out.println(i + "," + click);
		}
		asyncLoader.done();
		return bobcentres;
	}

	/* utility: */
	public static String prettyPoint(Point p) {
		return prettyPoint(p.x, p.y);
	}

	public static String prettyPoint(int x, int y) {
		return "(" + x + "," + y + ")";
	}
	
	public static String prettyPoint(PointD p) {
		return prettyPoint(p.x, p.y);
	}

	public static String prettyPoint(double x, double y) {
		return "(" + x + "," + y + ")";
	}
	
	public static List<Component> genLabel(String contents) {
		JLabel label = new JLabel(contents);
		List<Component> list = new ArrayList<Component>();
		list.add(label);
		return list;
	}
}
