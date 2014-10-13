package nutella.pendvid;

import java.awt.Point;

public class PointCalc {
	private PointD[] edgeVecs;
	
	private PointD intersection;
	
	public PointCalc(Point[] refLine, Point[][] edgeVecs) {
		this.intersection = intersection(edgeVecs[0], edgeVecs[1]);
		System.out.println("intersection of edgevecs at " +
				BobClick.prettyPoint(this.intersection));
	}
	
	private PointD intersection(Point[] p1, Point[] p2) {
		if(p1[0].x == p1[1].x) {
			if(p2[0].x == p2[1].x) {
				throw new RuntimeException("No intersection, parallel lines");
			}
			double m2 = (p2[1].y - p2[0].y) / (double) (p2[1].x - p2[0].x);
			double b2 = p2[0].y - p2[0].x * m2;

			return new PointD(p1[0].x, m2 * p1[0].x + b2);
		}
		
		double m1 = (p1[1].y - p1[0].y) / (double) (p1[1].x - p1[0].x);
		double b1 = p1[0].y - p1[0].x * m1;
		double m2 = (p2[1].y - p2[0].y) / (double) (p2[1].x - p2[0].x);
		double b2 = p2[0].y - p2[0].x * m2;

		System.out.println("line 1: (" + m1 + "," + b1 + ")");
		System.out.println("line 2: (" + m2 + "," + b2 + ")");
		
		double x = ((b2 - b1) / (m1 - m2));
		double y = (m1 * x + b1);
		return new PointD(x, y);
	}
}
