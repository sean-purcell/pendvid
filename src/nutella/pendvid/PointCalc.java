package nutella.pendvid;

import java.awt.Point;

import nutella.pendvid.bobclick.ClickData;

public class PointCalc {
	private PointD intersection;
	private PointD refVec;
	
	public PointCalc(Point[] refLine, Point[][] edgeVecs) {
		this.intersection = intersection(edgeVecs[0], edgeVecs[1]);
		System.out.println("intersection of edgevecs: " +
				Util.prettyPoint(this.intersection));
		this.refVec = calcRefVec(refLine);
		System.out.println("reference vert vec: " +
				Util.prettyPoint(this.refVec));
	}

	private PointD calcRefVec(Point[] refLine) {
		PointD vec = new PointD(refLine[1]).sub(new PointD(refLine[0]));
		if(intersection.d2(new PointD(refLine[1])) >
			intersection.d2(new PointD(refLine[0]))) {
			return vec.unit();
		} else {
			return vec.mul(-1).unit();
		}
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
	
	public ClickData compClick(Point click) {
		PointD c = new PointD(click);
		PointD vec = c.sub(intersection);
		double mag = vec.mag();
		double dot = refVec.dot(vec);
		double theta = Math.acos(dot / mag);
		if(refVec.x * vec.y - refVec.y * vec.x < 0) {
			theta = -theta;
		}
		double rtheta = theta - Math.PI/2;
		double x = mag * Math.cos(rtheta);
		double y = mag * Math.sin(rtheta);
		System.out.println("vec\t:" + Util.prettyPoint(vec));
		System.out.println("mag\t:" + mag);
		System.out.println("dot\t:" + dot);
		return new ClickData(theta, new PointD(x, y));
	}
}
