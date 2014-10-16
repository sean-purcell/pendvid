package nutella.pendvid.imgdiff;

import java.awt.Point;

public class DiffData {
	public final boolean[][] diff;
	public final Point avg;
	public final double stdev;

	public DiffData(boolean[][] diff, Point avg, double stdev) {
		this.diff = diff;
		this.avg = avg;
		this.stdev = stdev;
	}
}
