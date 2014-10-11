package nutella.pendvid;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GUI extends JFrame{
	private final JPanel root;
	private BufferedImage img;
	private JLabel show;

	public GUI() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.root = new JPanel();
		this.getContentPane().add(root);
		this.pack();
		this.setVisible(true);
		root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
		
		this.setResizable(true);
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				GUI.this.redraw();
			}
		});
	}
	
	public void redraw() {
		if(show != null)
			root.remove(show);
		Dimension size = root.getSize();
		double ratiox = size.getWidth() / img.getWidth();
		double ratioy = size.getHeight() / img.getHeight();
		double ratio = Math.min(ratiox, ratioy);
		show = new JLabel(new ImageIcon(
				img.getScaledInstance(
						(int) (img.getWidth() * ratio),
						(int) (img.getHeight() * ratio),
						0)));
		root.add(show);
		//this.repaint();
	}
	
	public Point[] getRefLine(BufferedImage img) {
		this.img = img;
		this.redraw();
		
		return new Point[]{new Point(1,1), new Point(1,2)};
	}
}
