package nutella.pendvid.bobclick;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

import nutella.pendvid.AsyncLoader;
import nutella.pendvid.PointCalc;
import nutella.pendvid.Util;

public class BobClick {
	public static void main(String[] args) {
		String dir = null;
		if(args.length == 0) {
			dir = Util.getOpenFilename(JFileChooser.DIRECTORIES_ONLY);
		} else {
			dir = args[0];
		}
		
		String outfile = "out.csv";
		PrintStream out = Util.getPrintStream(outfile);

		System.out.println(dir);

		int numFrames = Util.maxFrameNum(dir);

		BobClickGUI bobClickGUI = new BobClickGUI();
		Point[] adjust = bobClickGUI.getRefLine(
				Util.getImg(dir, 0),
				genLabel("Click on two points along a reference vertical line"));
		System.out.println("Reference vertical line: "
				+ Util.prettyPoint(adjust[0]) + " -- "
				+ Util.prettyPoint(adjust[1]));

		System.out.println("number of frames: " + numFrames);
		Point[][] edgevecs = getEdgeVecs(dir, numFrames, bobClickGUI);
		System.out.println("edgevec 1: "
				+ Util.prettyPoint(edgevecs[0][0]) + " -- "
				+Util.prettyPoint(edgevecs[0][1]));
		System.out.println("edgevec 2: "
				+ Util.prettyPoint(edgevecs[1][0]) + " -- "
				+Util.prettyPoint(edgevecs[1][1]));
		PointCalc pc = new PointCalc(adjust, edgevecs);
		getBobClicks(dir, numFrames, bobClickGUI, pc, out);
		
		out.close();
	}

	public static Point[][] getEdgeVecs(final String dir, final int numFrames, final BobClickGUI bobClickGUI) {
		List<Component> instr = genLabel(
			"Define the line for the pendulum swing at the two edges.  Press next to advance to the next image.");
		JButton button = new JButton("next");
		final AsyncLoader asyncLoader = Util.loadImgsAsync(dir, numFrames);
		button.addActionListener(new ActionListener() {
			private int i = 0;

			@Override
			public void actionPerformed(ActionEvent e1) {
				if(i + 1 >= numFrames)
					return;
				i++;
				bobClickGUI.setImg(asyncLoader.next());
			}
		});
		instr.add(button);
		Point[][] edgevecs = bobClickGUI.getEdgeVecs(Util.getImg(dir, 0), instr);
		asyncLoader.done();
		return edgevecs;
	}
	
	public static List<Point> getBobClicks(String dir, int numFrames, BobClickGUI bobClickGUI, PointCalc pc, PrintStream out) {
		List<Point> bobcentres = new ArrayList<Point>(numFrames);
		final AsyncLoader asyncLoader = Util.loadImgsAsync(dir, numFrames);
		asyncLoader.interrupt();
		for(int i = 0; i < numFrames; i++) {
			Point p = bobClickGUI.getBobClick(asyncLoader.next(), genLabel("Click on bob centre"));
			System.out.println("bob at:\t" + Util.prettyPoint(p));
			bobcentres.add(p);
			ClickData click = pc.compClick(p);
			System.out.println("data: " + click);
			out.println(i + "," + click);
		}
		asyncLoader.done();
		return bobcentres;
	}

	public static List<Component> genLabel(String contents) {
		JLabel label = new JLabel(contents);
		List<Component> list = new ArrayList<Component>();
		list.add(label);
		return list;
	}
}
