package nutella.pendvid;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImgAnalyze {
    public boolean[][] diff(BufferedImage a, BufferedImage b) {
        boolean[][] d = new boolean[a.getWidth()][a.getHeight()];
        for (int i = 0; i < a.getWidth(); i++) {
            for (int j = 0; j < a.getHeight(); j++) {
                Color o = new Color(a.getRGB(i, j));
                Color n = new Color(b.getRGB(i, j));
                d[i][j] = (o.getRed() - n.getRed()) * (o.getRed() - n.getRed()) +
                        (o.getBlue() - n.getBlue()) * (o.getBlue() - n.getBlue()) +
                      (o.getGreen() - n.getGreen()) * (o.getGreen() - n.getGreen()) > 400;
            }
        }
        return d;
    }
}
