package nutella.pendvid.imgdiff;

import java.awt.Point;
import java.util.List;

import nutella.pendvid.PointD;

public class DiffData {
	public final List<Point> points;
	public final PointD avg;
	public final double stdev;

	public DiffData(List<Point> points, PointD avg, double stdev) {
		this.points = points;
		this.avg = avg;
		this.stdev = stdev;
	}
}
