package nutella.pendvid.imgdiff;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class DiffDisp extends JPanel {
	private BufferedImage img;
	private DiffData diff;
	private double ratio;
	private ImgDiffGUI gui;
	
	protected void update(BufferedImage img, DiffData diff, ImgDiffGUI gui) {
		this.img = img;
		this.diff = diff;
		this.gui = gui;
		this.validate();
		this.repaint();
	}

	protected void update(BufferedImage img, ImgDiffGUI gui) {
		this.img = img;
		this.diff = null;
		this.gui = gui;
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
		scratch.setColor(Color.WHITE);
		scratch.fillRect(0, 0, img.getWidth(), img.getHeight());
		System.out.println("mode: " + gui.mode);
		switch(gui.mode) {
		case 1:
			scratch.drawImage(img, 0, 0, null);
			scratch.setColor(new Color(0, 0, 0, 196));
			for(int x = 0; x < diff.diff.length; x++) {
				for(int y = 0; y < diff.diff[x].length; y++) {
					if(diff.diff[x][y]) {
						scratch.fillRect(x, y, 1, 1);
					}
				}
			}
			scratch.setColor(Color.RED);
			final int DOT_SIZE = 3;
			scratch.fillOval(diff.avg.x - DOT_SIZE, diff.avg.y - DOT_SIZE, DOT_SIZE * 2, DOT_SIZE * 2);
			scratch.drawOval((int) (diff.avg.x - diff.stdev), (int) (diff.avg.y - diff.stdev),
					(int) (2 * diff.stdev), (int) (2 * diff.stdev));
			break;
		case 2:
			scratch.drawImage(img, 0, 0, null);
			if(gui.click1 != null && gui.mouse != null) {
				Point c1 = gui.click1;
				Point m = gui.mouse;
				int dx = m.x - c1.x;
				int dy = m.y - c1.y;
				System.out.println("dx,dy: " + dx + "," + dy);
				if(dx > 0 && dy > 0) {
					scratch.setColor(Color.RED);
					scratch.drawRect(c1.x, c1.y,
							dx, dy);
				}
			}
			break;
		}

		g.drawImage(bi.getScaledInstance(
				(int) (img.getWidth() * ratio),
				(int) (img.getHeight() * ratio), 0), 0, 0, null);
	}
	
	public double getRatio() {
		return ratio;
	}
	
	public int width() {
		return (int) (ratio * img.getWidth());
	}
	
	public int height() {
		return (int) (ratio * img.getWidth());
	}
}
