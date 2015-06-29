package com.drmattyg.nanokaraoke.video;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.drmattyg.nanokaraoke.MidiFile;
import com.drmattyg.nanokaraoke.TrackChunk;
import com.drmattyg.nanokaraoke.TrackEvent;

public class KaraokeScreen {
	
	private List<KaraokeLine> lines;
	private BufferedImage img;
	public static final int LINES_TO_RENDER = 2;
	private static final int HORIZONTAL_PADDING = 20;
	private static final float STROKE_WIDTH = 3; 
	private static final Color SUNG_COLOR = Color.CYAN;
	private static final Color UNSUNG_COLOR = Color.MAGENTA;
	private Font font = new Font("Verdana", Font.BOLD, 48);
	private int topLineIndex;
	private int currentLineIndex;
	private int lyricOffset;
	protected KaraokeScreen() {}
	public static KaraokeScreen getInstance(BufferedImage img, List<KaraokeLine> lyrics, int topLineIndex, int currentLineIndex, int lyricOffset) {
		KaraokeScreen s = new KaraokeScreen();
		s.lines = new ArrayList<KaraokeLine>(lyrics);
		s.img = img;
		s.topLineIndex = topLineIndex;
		s.currentLineIndex = currentLineIndex;
		s.lyricOffset = lyricOffset;
		return s;
	}
	
	public void setFont(Font f) { font = new Font(f.getFontName(), f.getStyle(), f.getSize()); }
	
	public Point2D calculateOffset(String text, int lineNumber) {
		int height = img.getHeight();
		int width = img.getWidth();
		Graphics2D g = img.createGraphics();
		g.setFont(font);
		g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING, 
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		float sf = img.getWidth()*1.0f/g.getFontMetrics().stringWidth(KaraokeLine.LINE_SCALER);
		int th = g.getFontMetrics().getHeight();
//		int verticalPadding = (int)Math.ceil(th*1.0/3);
		
		// seems like the vertical padding is unnecessary, it's factored into the text height, which I think is overgenerous to begin with.  But it seems to be working okay.
		// Not going to waste time over-tweaking it for the moment.
		int verticalPadding = 0; // 
		
		int tw = g.getFontMetrics().stringWidth(text);
		int y = height - (th + verticalPadding)*lineNumber + verticalPadding;
		int x = Math.round((width - (tw + 2*HORIZONTAL_PADDING)*sf)/2);
		return new Point2D.Float(x,  y);
	}
	
	public float calculateScaleFactor(String text) {
		Graphics2D g = img.createGraphics();
		g.setFont(font);
		float sf = img.getWidth()*1.0f/g.getFontMetrics().stringWidth(KaraokeLine.LINE_SCALER);
		return sf;
	}
	
	private void drawText(String text, Color fillColor, float scaleFactor, Point2D offset) {
		Graphics2D g = img.createGraphics();
		g.translate(offset.getX(), offset.getY());
		g.scale(scaleFactor, scaleFactor);
		TextLayout tl = new TextLayout(text, font, g.getFontRenderContext());
		Shape shape = tl.getOutline(null);
		g.setColor(fillColor);
		g.fill(shape);
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(STROKE_WIDTH));
		g.draw(shape);
		
	}
	
	public BufferedImage render() {
		for(int i = topLineIndex; i < topLineIndex + LINES_TO_RENDER; i++) {
			KaraokeLine kLine = lines.get(i);
			float scaleFactor = calculateScaleFactor(kLine.toString());
			Point2D offset = calculateOffset(kLine.toString(), topLineIndex + LINES_TO_RENDER - i);
			Color lineColor;
			if(currentLineIndex > topLineIndex && i < currentLineIndex) {
				lineColor = SUNG_COLOR;
			} else {
				lineColor = UNSUNG_COLOR;
			}
			drawText(kLine.toString(), lineColor, scaleFactor, offset);
			if(i == currentLineIndex && lyricOffset >= 0) {
				String partial = kLine.subString(lyricOffset);
				drawText(partial, SUNG_COLOR, scaleFactor, offset);
			}
		}

		return img;
	}
	

	
	
}
