package nutella;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class Pendvid {
	public static void main(String[] args) {
		if(args.length < 2) {
			throw new IllegalArgumentException
				("must pass filename as first argument and out dir as second");
		}

		Readvid.readvid(args[0], args[1]);
	}
}

class VidFrame extends JPanel {
	private BufferedImage img;
	public void setImg(BufferedImage img) {
		this.img = img;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		if(img != null)
			g.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), null);
	}
}
