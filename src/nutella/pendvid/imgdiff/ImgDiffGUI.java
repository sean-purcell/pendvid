package nutella.pendvid.imgdiff;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ImgDiffGUI extends JFrame {
	private final JPanel root;
	private final DiffDisp diffDisp;

	private Thread waitThread;
	
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
				if(waitThread != null) {
					waitThread.interrupt();
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
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
	}
	
	public void display(BufferedImage img, boolean[][] diff) {
		diffDisp.update(img, diff);
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
}
