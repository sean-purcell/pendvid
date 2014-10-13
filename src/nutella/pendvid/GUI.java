package nutella.pendvid;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.List;

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
		Dimension size = imgclick.getSize();
		double ratiox = size.getWidth() / img.getWidth();
		double ratioy = size.getHeight() / img.getHeight();
		return Math.min(ratiox, ratioy);
	}

	public void redraw() {
		if(img == null)
			return;
		imgclick.setImg(img, imgRatio(img));
	}

	public Point[] getRefLine(BufferedImage img, List<Component> instr) {
		this.img = img;
		for(int i = 0; i < instr.size(); i++) {
			root.add(instr.get(i));
		}
		root.repaint();
		Point[] line = imgclick.getRefLine(img, imgRatio(img));
		for(int i = 0; i < instr.size(); i++) {
			root.remove(instr.get(i));
		}
		root.repaint();
		return line;
	}
	
	public Point getBobClick(BufferedImage img, List<Component> instr) {
		this.img = img;
		for(int i = 0; i < instr.size(); i++) {
			root.add(instr.get(i));
		}
		root.repaint();
		Point click = imgclick.getBobClick(img, imgRatio(img)); 
		for(int i = 0; i < instr.size(); i++) {
			root.remove(instr.get(i));
		}
		root.repaint();
		return click;
	}

	public void setImg(BufferedImage img) {
		this.img = img;
		imgclick.setImg(img, imgRatio(img));
	}
}
