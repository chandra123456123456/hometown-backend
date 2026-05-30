package com.hometown.product.image;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Service
public class WatermarkService {

    public byte[] apply(byte[] imageBytes, String text) {
        try {
            BufferedImage src = ImageIO.read(new ByteArrayInputStream(imageBytes));
            if (src == null) return imageBytes;

            int w = src.getWidth();
            int h = src.getHeight();
            BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = out.createGraphics();
            g.drawImage(src, 0, 0, null);

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
            g.setColor(Color.WHITE);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(16, w / 15)));

            FontMetrics fm = g.getFontMetrics();
            int textW = fm.stringWidth(text);
            int textH = fm.getAscent();
            int stepX = textW + 60;
            int stepY = textH + 50;

            AffineTransform original = g.getTransform();
            for (int y = -stepY; y < h + stepY; y += stepY) {
                for (int x = -stepX; x < w + stepX; x += stepX) {
                    AffineTransform t = AffineTransform.getRotateInstance(
                            Math.toRadians(-30), x + textW / 2.0, y + textH / 2.0);
                    g.setTransform(t);
                    g.drawString(text, x, y + textH);
                }
            }
            g.setTransform(original);
            g.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(out, "png", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            return imageBytes;
        }
    }
}
