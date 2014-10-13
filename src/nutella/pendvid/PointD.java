package nutella.pendvid;

import java.awt.Point;

public class PointD {
	public final double x, y;
	public PointD(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public PointD(Point a) {
		this(a.x, a.y);
	}
	
	public PointD sub(PointD that) {
		return new PointD(x - that.x, y - that.y);
	}
	
	public PointD add(PointD that) {
		return new PointD(x + that.x, y + that.y);
	}
	
	public PointD mul(double scale) {
		return new PointD(x * scale, y * scale);
	}
	
	public PointD unit() {
		return mul(1/mag());
	}
	
	public double d2(PointD that) {
		double dx = x - that.x;
		double dy = y - that.y;
		return dx * dx + dy * dy;
	}
	
	public double dot(PointD that) {
		return x * that.x + y * that.y;
	}
	
	public double mag() {
		return Math.sqrt(d2(new PointD(0, 0)));
	}
}
