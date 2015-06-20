package com.drmattyg.nanokaraoke.video;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;

public class TextUtils {
	public static void drawOutlinedText(BufferedImage img, String text, Color outline, Color fill, int x, int y, Font font) {
		Graphics2D g = img.createGraphics();
		g.setColor(fill);
		g.setFont(font);
		FontRenderContext frc = g.getFontRenderContext();
		g.drawString(text, x, y);
		g.setColor(outline);
		TextLayout tl = new TextLayout(text, font, frc);
		Shape textShape = tl.getOutline(null);
		g.setStroke(new BasicStroke(2));
		g.translate(x, y);
		g.draw(textShape);
	}
}
