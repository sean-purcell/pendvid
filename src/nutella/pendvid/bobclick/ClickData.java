package nutella.pendvid.bobclick;

import nutella.pendvid.PointD;

public class ClickData {
	public final double theta;
	public final PointD vec;

	public ClickData(double theta, PointD vec) {
		this.theta = theta;
		this.vec = vec;
	}
	
	public String toString() {
		return theta + "," + vec.x + "," + vec.y;
	}
}
