package nutella.pendvid;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

public class BobClick {
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
			img = ImageIO.read(new File(dir + File.separator + "f" + num + ".jpg"));
		} catch(IOException e) {
			throw new RuntimeException("could not load image file");
		}
		return img;
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
	}
}
