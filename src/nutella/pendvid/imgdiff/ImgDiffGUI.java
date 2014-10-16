package nutella.pendvid.imgdiff;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import nutella.pendvid.AsyncLoader;
import nutella.pendvid.Util;

@SuppressWarnings("serial")
public class ImgDiffGUI extends JFrame {
	private final JPanel root;
	protected final DiffDisp diffDisp;
	
	private Thread waitThread;
	
	protected int mode;
	protected Point click1, click2, mouse;
	
	public ImgDiffGUI() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.root = new JPanel();
		this.diffDisp = new DiffDisp();
		this.root.add(diffDisp);
		this.getContentPane().add(root);
		this.pack();
		this.setVisible(true);
		root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));

		this.setResizable(true);
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				System.out.println("Resized");
				ImgDiffGUI.this.redraw();
			}
		});
		
		diffDisp.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println("mouse click at: "
						+ Util.prettyPoint(e.getX(), e.getY()));
				System.out.println("mode: " + mode);
				switch(mode) {
				case 1:
					if(waitThread != null) {
						waitThread.interrupt();
					}
					break;
				case 2:
					if(e.getButton() == MouseEvent.BUTTON1) {
						if(click1 == null) {
							double ratio = diffDisp.getRatio();
							click1 = new Point(
									(int) (e.getX() / ratio),
									(int) (e.getY() / ratio));
						} else {
							double ratio = diffDisp.getRatio();
							click2 = new Point(
									(int) (e.getX() / ratio),
									(int) (e.getY() / ratio));
							int dx = click2.x - click1.x;
							int dy = click2.y - click1.y;
							if(dx <= 0 || dy <= 0) {
								click2 = null;
							}
						}
					} else {
						click1 = null;
					}
					break;
				}
				ImgDiffGUI.this.redraw();
			}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}
			
		});
		
		diffDisp.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {
				mouseMoved(e);
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				double ratio = diffDisp.getRatio();
				ImgDiffGUI.this.mouse = new Point(
						(int) (e.getX() / ratio),
						(int) (e.getY() / ratio));
				ImgDiffGUI.this.redraw();
			}
			
		});
		
		this.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				System.out.println("key pressed");
				if(waitThread != null) {
					waitThread.interrupt();
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {}
			
		});
	}
	
	private double imgRatio(BufferedImage img) {
		Dimension size = diffDisp.getSize();
		double ratiox = size.getWidth() / img.getWidth();
		double ratioy = size.getHeight() / img.getHeight();
		return Math.min(ratiox, ratioy);
	}
	
	public void redraw() {
		diffDisp.setRatio(imgRatio(diffDisp.getImg()));
		diffDisp.repaint();
		root.validate();
		root.repaint();
		this.validate();
		this.repaint();
	}
	
	public void display(BufferedImage img, DiffData diff) {
		diffDisp.update(img, diff, this);
		this.redraw();
		waitThread = Thread.currentThread();
		while(!Thread.interrupted()) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}		
		return;
	}
	
	public Point[] getBoundBox(final AsyncLoader imgLoader) {
		this.mode = 2;
		this.click1 = null;
		this.click2 = null;
		diffDisp.update(imgLoader.next(), this);
		JLabel instr = new JLabel("Define the bounding box");
		JButton next = new JButton("next");
		next.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				diffDisp.update(imgLoader.next(), ImgDiffGUI.this);
			}
		});
		this.root.add(instr);
		this.root.add(next);
		this.redraw();
		this.waitThread = Thread.currentThread();
		while(click2 == null) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {
			}
		}
		this.root.remove(instr);
		this.root.remove(next);
		this.mode = 0;
		imgLoader.done();
		this.waitThread = null;
		this.redraw();
		return new Point[]{click1, click2};
	}
}
