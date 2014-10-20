package nutella.pendvid;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.List;

import nutella.pendvid.imgdiff.DiffData;

public class ImgAnalyze {
    /**
     * A value that is the maximum by which two points in a cube
     * with side length 255 representing RGB can be separated
     * (Cartesian) before they are considered different
     */
	public static int THRESHOLD = 30;

    /**
     * Finds the differences between two images
     * @param a The old image. (Or the new one, it doesn't really matter)
     * @param b The new image. (Or the old one, it doesn't really matter)
     * @return An array of pixels that has the same size as the images.
     * True = Pixels are different
     * False = Pixels are "similar"
     */
    public static boolean[][] pxDiff(BufferedImage a, BufferedImage b) {
        boolean[][] d = new boolean[a.getWidth()][a.getHeight()];//declare array
        for (int i = 0; i < a.getWidth(); i++) {//look through x
            for (int j = 0; j < a.getHeight(); j++) {//loop through y
                Color o = new Color(a.getRGB(i, j));//old colour
                Color n = new Color(b.getRGB(i, j));//new colour
                /**
                 * compares if the square of the cartesian distance is larger
                 * than the square of the threshold
                 * note that the square of the cartesian distance is the sum
                 * of the squares of the differences along each axis
                 * this is why we take the difference in Red
                 * square it
                 * add it to the square of the difference in green
                 * and do the same for blue
                 * then compare
                 */
                d[i][j] = (o.getRed() - n.getRed()) * (o.getRed() - n.getRed()) +
                        (o.getBlue() - n.getBlue()) * (o.getBlue() - n.getBlue()) +
                      (o.getGreen() - n.getGreen()) * (o.getGreen() - n.getGreen()) >
                	THRESHOLD * THRESHOLD;
            }
        }
        return d;
    }

    /**
     * Find the average point from a list of points
     * @param points a list of points
     * @return their average
     */
    public static PointD avg(List<Point> points) {
        double x = 0, y = 0;//initialize at 0
        for (Point p : points) {//add x and y values of every point
            x += p.x;
            y += p.y;
        }
        x /= points.size();//divide by number of points to average
        y /= points.size();
        return new PointD(x, y);//return
    }

    /**
     * finds the standard deviation from a list of points
     * @param points a list of points
     * @return the standard deviation as a cartesian distance
     */
    public static double stdev(List<Point> points) {
        PointD avg = avg(points);//finds the average point
        double d = 0;//initialize counter
        for (Point p : points) {//for every point
            /**
             * note that stdev is the square root of the average sum of the squares
             * the square root and division by number of points is done
             * in the return statement
             * note that the square of the cartesian distance is the sum
             * of the squares along each axis
             * hence we take the square of the difference of every point and the
             * average along each axis and add it to the counter
             */
            d += (p.x - avg.x) * (p.x - avg.x) + (p.y - avg.y) * (p.y - avg.y);
        }
        return Math.sqrt(d / points.size());//return (see lines 78-80)
    }

    /**
     * A method that finds the position of the pendulum based on two images.
     * @param a The old image. (Or the new one, it doesn't really matter)
     * @param b The new image. (Or the old one, it doesn't really matter)
     * @param prevDiff What this method returned one image ago.
     * @return Three values: the points that matter, their average, their stdev
     */
    public static DiffData analyzeImgDiff(BufferedImage a, BufferedImage b, DiffData prevDiff) {
        //a boolean array of all the pixels
        //size of image in pixels
        //true = pixels are different
        //false = pixels did not appreciably change
        boolean[][] diff = pxDiff(a, b);
        List<Point> points = Util.convert(diff);//convert above array to a list of points that changed
        if(prevDiff == null) {//this is here for the very first frame
        	return new DiffData(points, avg(points), stdev(points));
        }
        /**
         * this value is used to prune out the hook (see lines 147-152)
         * it is approximately equal to the radius of a bob in pixels
         */
        final int RADIUS = 20;
        /**
         * this value is also used to prune out the hook (see lines 147-152)
         * it is approximately equal to the number of pixels one can
         * expect to find in a quarter of a circle of radius RADIUS
         */
        final int BOUND = 300;
        for (int i = 0; i < points.size(); i++) {//go through every point
            Point p = points.get(i);//current point
            int c = 0;//number of points in a circle of radius RADIUS around given point
            /**
             * this loop only bothers checking the x values that are within RADIUS
             * of the current point
             * the comparative statements are there in case the point is near a
             * boundary and this prevents it from throwing
             * ArrayIndexOutOfBoundsException
             */
            for (int x = Math.max(p.x - RADIUS, 0); x < Math.min(p.x + RADIUS, diff.length); x++) {
                /**
                 * this convoluted statement was designed to save on time
                 * instead of checking in a square of size 2RADIUS on either side
                 * then checking if the point is within the circle
                 * this does something to similar as what was done for lab 1 to get
                 * rid of the conditional statement in the integral
                 * it sets the bounds for y such that they vary as a function of x
                 * and catch the correct width of the circle
                 */
                for (int y = Math.max(p.y - (int) Math.sqrt(RADIUS * RADIUS - (p.x - x) * (p.x - x)), 0);
                     y < Math.min(p.y + (int) Math.sqrt(RADIUS * RADIUS - (p.x - x) * (p.x - x)), diff[0].length); y++) {
                    if (diff[x][y]) {//if a pixel in the circle is different
                        c++;//increment the counter
                    }
                }
            }
            /**
             * after the number of points in the surrounding circle has been counted
             * this checks if it exceeds about a quarter
             * this is used to get rid of things like the hook that do not have
             * many points around them yet keep points that are corners
             * of the pendulum
             * for the point to be kept, the counter, c, must exceed BOUND
             */
            if (c < BOUND) {
                points.remove(i);//remove point if condition failed
                i--;
                diff[p.x][p.y] = false;
            }
        }
        PointD avg = avg(points);//calculate average
        double stdev = stdev(points);//calculate stdev
        for (int i = 0; i < points.size(); i++) {//go through every point
            /**
             * this calculates the square of the cartesian distance of the
             * point from the average of all the points
             * if the point is more than two standard deviations away from the average
             * it is ignored
             */
            if ((points.get(i).x - avg.x) * (points.get(i).x - avg.x) +
                (points.get(i).y - avg.y) * (points.get(i).y - avg.y) > 4 * stdev * stdev) {
                points.remove(i);
                i--;
            }
        }
        return new DiffData(points, avg(points), stdev(points));//return and recalculate
    }
}
