package nutella.pendvid.imgdiff;

import java.awt.Point;
import java.util.List;

public class DiffData {
	public final List<Point> points;
	public final Point avg;
	public final double stdev;

	public DiffData(List<Point> points, Point avg, double stdev) {
		this.points = points;
		this.avg = avg;
		this.stdev = stdev;
	}
}
