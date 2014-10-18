package nutella.pendvid.bobclick;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import nutella.pendvid.Util;

@SuppressWarnings("serial")
public class ImgClicker extends JPanel {
	private Image img;
	private double ratio;

	// 0 is normal;
	// 1 is refline;
	// 2 is bobclick;
	private int mode;

	private volatile Point click1, click2, mouse;

	private volatile Thread waitThread;

	public ImgClicker() {
		mode = 0;
		this.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {
				mouseClick(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}
	
		});
		this.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
				mouse = new Point((int) (e.getX() / ratio), (int) (e.getY() / ratio));
				ImgClicker.this.repaint();
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				System.out.println("mouse dragged: "
						+ Util.prettyPoint(mouse.x, mouse.y) + " -- "
						+ Util.prettyPoint(e.getX(), e.getY()));
				mouseMoved(e);
			}
		});
	}

	private void mouseClick(MouseEvent e) {
		System.out.println("click at:\t" + Util.prettyPoint(e.getX(), e.getY()));
		if(e.getX() >= img.getWidth(null) || e.getY() >= img.getHeight(null)) {
			return;
		}
		int x = (int) (e.getX() / ratio);
		int y = (int) (e.getY() / ratio);
		System.out.println("rescaled to:\t" + Util.prettyPoint(x, y));
		switch(mode) {
		case 0: break;
		case 1: {
			if(e.getButton() == MouseEvent.BUTTON1) {
				if(click1 == null) {
					click1 = new Point(x, y);
					System.out.println("refline click1");
				} else {
					Point tmp = new Point(x, y);
					if(!tmp.equals(click1)) {
						click2 = tmp;
						System.out.println("refline click2");
						if(waitThread != null) {
							waitThread.interrupt();
						}
					}
				}
			} else {
				click1 = null;
			}
		} break;
		case 2: {
			if(e.getButton() == MouseEvent.BUTTON1) {
				click1 = new Point(x, y);
				System.out.println("bobclick");
				if(waitThread != null) {
					waitThread.interrupt();
				}
			}
		} break;
		}
		ImgClicker.this.repaint();
	}
	
	private int appRatio(int v) {
		return (int) (v * ratio);
	}

	public Point[] getLine(BufferedImage frame, double ratio) {
		this.setImg(frame, ratio);
		this.click1 = null;
		this.click2 = null;
		this.mode = 1;
		Thread.interrupted();
		waitThread = Thread.currentThread();
		while(click2 == null) {
			if(Thread.interrupted()) {
				continue;
			}
			try{
				Thread.sleep(1000);
			} catch(InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		this.mode = 0;
		waitThread = null;
		return new Point[]{click1, click2};
	}

	public Point getBobClick(BufferedImage frame, double ratio) {
		this.setImg(frame, ratio);
		this.click1 = null;
		this.mode = 2;
		Thread.interrupted();
		waitThread = Thread.currentThread();
		while(click1 == null) {
			if(Thread.interrupted()) {
				continue;
			}
			try{
				Thread.sleep(1000);
			} catch(InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		this.mode = 0;
		waitThread = null;
		return click1;
	}

	public void setImg(BufferedImage newImg, double ratio) {
		if(newImg == null)
			return;
		System.out.println("updated ratio: " + ratio);
		this.ratio = ratio;
		this.img = newImg.getScaledInstance(
				(int) (newImg.getWidth() * ratio),
				(int) (newImg.getHeight() * ratio),
				0);
		this.setMinimumSize(new Dimension(
				img.getWidth(null),
				img.getHeight(null)));
		this.repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		if(img != null) {
			g.drawImage(img, 0, 0, img.getWidth(null), img.getHeight(null), null);
		}
		switch(mode) {
		case 0: break;
		case 1: {
			if(click1 == null) break;
			g.setColor(Color.RED);
			g.drawLine(appRatio(click1.x), appRatio(click1.y) - 5,
					appRatio(click1.x), appRatio(click1.y) + 5);
			g.drawLine(appRatio(click1.x) - 5, appRatio(click1.y),
					appRatio(click1.x) + 5, appRatio(click1.y));
			if(click2 == null && mouse != null) {
				g.drawLine(appRatio(click1.x), appRatio(click1.y),
						appRatio(mouse.x), appRatio(mouse.y));
			}
		} break;
		case 2: break;
		}
	}
}
