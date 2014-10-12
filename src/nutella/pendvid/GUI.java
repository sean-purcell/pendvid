package nutella.pendvid;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GUI extends JFrame{
	private final JPanel root;
	private BufferedImage img;
	private ImgClicker imgclick;
	private JLabel show;

	public GUI() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.root = new JPanel();
		imgclick = new ImgClicker();
		root.add(imgclick);
		this.getContentPane().add(root);
		this.pack();
		this.setVisible(true);
		root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
		
		this.setResizable(true);
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				System.out.println("Resized");
				GUI.this.redraw();
			}
		});
	}
	
	private double imgRatio(BufferedImage img) {
		Dimension size = root.getSize();
		double ratiox = size.getWidth() / img.getWidth();
		double ratioy = size.getHeight() / img.getHeight();
		return Math.min(ratiox, ratioy);
	}
	
	public void redraw() {
		if(img == null)
			return;
		imgclick.setImg(img, imgRatio(img));
	}
	
	public Point[] getRefLine(BufferedImage img) {
		this.img = img;
		return imgclick.getRefLine(img, imgRatio(img));
	}
}
