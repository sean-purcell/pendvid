package nutella.pendvid.imgdiff;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.PrintStream;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import nutella.pendvid.AsyncLoader;
import nutella.pendvid.ImgAnalyze;
import nutella.pendvid.Util;

@SuppressWarnings("serial")
public class ImgDiff extends JFrame {
	public static void main(String[] args) {
		String dir = null, out = null;
		
		switch(args.length) {
		case 0:
			dir = Util.getOpenFilename(JFileChooser.DIRECTORIES_ONLY);
			out = Util.getWriteFilename(JFileChooser.FILES_ONLY);
			break;
		case 1:
			dir = args[0];
			out = Util.getWriteFilename(JFileChooser.FILES_ONLY);
			break;
		default:
			dir = args[0];
			out = args[1];
			break;
		}
		
		PrintStream fout = Util.getPrintStream(out);
		ImgDiffGUI gui = new ImgDiffGUI();
		int maxFrames = Util.maxFrameNum(dir);
		Point[] bounds = gui.getBoundBox(Util.loadImgsAsync(dir, maxFrames));
		AsyncLoader asyncLoader = Util.loadImgsAsync(dir, maxFrames);
		BufferedImage prev = Util.crop(asyncLoader.next(), bounds);
		DiffData prevDiff = null;
		gui.mode = 1;
		for(int i = 1; i < maxFrames; i++) {
			BufferedImage cur = Util.crop(asyncLoader.next(), bounds);
			DiffData diff = ImgAnalyze.analyzeImgDiff(prev, cur, prevDiff);
			gui.display(cur, diff);
			prev = cur;
			prevDiff = diff;
			
			fout.println(i + ","
					+ diff.avg.x + ","
					+ diff.avg.y + ",");
		}
		asyncLoader.done();
		gui.mode = 0;
		fout.close();
		gui.dispose();
	}
}
