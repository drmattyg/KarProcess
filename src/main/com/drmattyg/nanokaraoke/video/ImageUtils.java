package com.drmattyg.nanokaraoke.video;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ImageUtils {
	public static BufferedImage copy(BufferedImage input) {
		return new BufferedImage(input.getWidth(), input.getHeight(), input.getType());
	}
	
	public static BufferedImage fade(BufferedImage input, float alpha) {
		// 0 = black, 1 = full image
		BufferedImage result = copy(input);
		Graphics2D g = result.createGraphics();
		g.setBackground(Color.BLACK);
		g.clearRect(0, 0, input.getWidth(), input.getHeight());
		AlphaComposite a = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,  alpha);
		g.setComposite(a);
		g.drawImage(input, 0, 0, null);
		return result;
	}
}
