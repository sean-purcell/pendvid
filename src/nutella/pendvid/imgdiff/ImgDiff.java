package nutella.pendvid.imgdiff;

import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentLinkedQueue;

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
		ConcurrentLinkedQueue<BufferedImage> imgq =
				new ConcurrentLinkedQueue<BufferedImage>();
		AsyncLoader asyncLoader = Util.loadImgsAsync(imgq, dir, maxFrames);
		while(imgq.size() == 0) {
			try{
				Thread.sleep(50);
			} catch(InterruptedException e) {
			}
		}
		BufferedImage prev = imgq.remove();
		asyncLoader.interrupt();
		for(int i = 1; i < maxFrames; i++) {
			while(imgq.size() == 0) {
				try{
					Thread.sleep(50);
				} catch(InterruptedException e) {
				}
			}
			BufferedImage cur = imgq.remove();
			asyncLoader.interrupt();
			gui.display(cur, ImgAnalyze.diff(prev, cur, 30));
			prev = cur;
		}
	}
}
