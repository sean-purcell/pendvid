package nutella.pendvid;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class ImgAnalyze {
    public static boolean[][] diff(BufferedImage a, BufferedImage b, int thresh) {
        boolean[][] d = new boolean[a.getWidth()][a.getHeight()];
        for (int i = 0; i < a.getWidth(); i++) {
            for (int j = 0; j < a.getHeight(); j++) {
                Color o = new Color(a.getRGB(i, j));
                Color n = new Color(b.getRGB(i, j));
                d[i][j] = (o.getRed() - n.getRed()) * (o.getRed() - n.getRed()) +
                        (o.getBlue() - n.getBlue()) * (o.getBlue() - n.getBlue()) +
                      (o.getGreen() - n.getGreen()) * (o.getGreen() - n.getGreen()) >
                	thresh * thresh;
            }
        }
        return d;
    }

    public static Point avg(boolean[][] a) {
        return avg(Util.convert(a));
    }

    public static Point avg(List<Point> points) {
        double x = 0, y = 0;
        for (Point p : points) {
            x += p.x;
            y += p.y;
        }
        x /= points.size();
        y /= points.size();
        return new Point((int) Math.round(x), (int) Math.round(y));
    }
}
