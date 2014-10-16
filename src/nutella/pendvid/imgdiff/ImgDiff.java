package nutella.pendvid.imgdiff;

import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import nutella.pendvid.AsyncLoader;
import nutella.pendvid.ImgAnalyze;
import nutella.pendvid.Util;

@SuppressWarnings("serial")
public class ImgDiff extends JFrame {
	public static void main(String[] args) {
		String dir = null;
		if(args.length == 0) {
			dir = Util.getFilename();
		} else {
			dir = args[0];
		}

		ImgDiffGUI gui = new ImgDiffGUI();
		int maxFrames = Util.maxFrameNum(dir);
		Point[] bounds = gui.getBoundBox(Util.loadImgsAsync(dir, maxFrames));
		AsyncLoader asyncLoader = Util.loadImgsAsync(dir, maxFrames);
		BufferedImage prev = asyncLoader.next();
		for(int i = 1; i < maxFrames; i++) {
			BufferedImage cur = asyncLoader.next();
			gui.display(cur, ImgAnalyze.diff(prev, cur, 30));
			prev = cur;
		}
		asyncLoader.done();
	}
}
