package nutella.pendvid.imgdiff;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class DiffDisp extends JPanel {
	private BufferedImage img;
	private boolean[][] diff;
	private double ratio;
	
	protected void update(BufferedImage img, boolean[][] diff) {
		this.img = img;
		this.diff = diff;
		this.validate();
		this.repaint();
	}

	protected void setRatio(double ratio) {
		this.ratio = ratio;
	}
	
	protected BufferedImage getImg() {
		return img;
	}

	public void paintComponent(Graphics g) {
		if(img == null) {
			return;
		}

		BufferedImage bi = new BufferedImage(
				img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics scratch = bi.getGraphics();
		scratch.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), null);
		scratch.setColor(new Color(0, 0, 0, 196));
		for(int x = 0; x < diff.length; x++) {
			for(int y = 0; y < diff[x].length; y++) {
				if(diff[x][y]) {
					scratch.fillRect(x, y, 1, 1);
				}
			}
		}
		g.drawImage(bi.getScaledInstance(
				(int) (img.getWidth() * ratio),
				(int) (img.getHeight() * ratio), 0), 0, 0, null);
	}
}
