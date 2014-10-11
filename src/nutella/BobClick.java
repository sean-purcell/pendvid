package nutella;

import java.io.File;

import javax.swing.JFileChooser;

public class BobClick {
	public static String getFilename() {
		JFileChooser jfc = new JFileChooser();
		int res = jfc.showOpenDialog(null);
		if(res == JFileChooser.CANCEL_OPTION) {
			throw new RuntimeException("Must choose file");
		}
		return jfc.getSelectedFile().getAbsolutePath();
	}
	
	public static void main(String[] args) {
		String filename = null;
		if(args.length == 0) {
			filename = getFilename();
		} else {
			filename = args[0];
		}
		
		System.out.println(filename);
	}
}
