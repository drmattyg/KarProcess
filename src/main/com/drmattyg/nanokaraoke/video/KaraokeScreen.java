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

public class KaraokeScreen {
	
	public static class KaraokeLine implements Iterable<Entry<Long, String>> {
		List<Entry<Long, String>> lyrics;
		public static final int MAX_LINE_LENGTH = 50;
		public static final String LINE_SCALER = "Take me to the river, drop me in the water. Drop me down. Abc"; // 60 letters.  Scaling is going to be approximate

		@Override
		public Iterator<Entry<Long, String>> iterator() { return lyrics.iterator(); }
		
		public KaraokeLine() {
			lyrics = new ArrayList<Entry<Long, String>>();
		}
		
		public KaraokeLine addLyric(long timestamp, String lyric) {
			lyrics.add(new SimpleEntry<Long, String>(timestamp, lyric));
			return this;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for(Entry<Long, String> entry : lyrics) {
				sb.append(entry.getValue());
			}
			return sb.toString().trim();
		}
		
		public String subString(int toOffset) {
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i <= toOffset; i++) {
				sb.append(lyrics.get(i).getValue());
			}
			return sb.toString().trim();
		}
		
		public boolean isEmpty() {
			return lyrics.isEmpty();
		}
		
		// the timeoffset at which this line starts
		public long getTimeOffset() {
			return lyrics.get(0).getKey();
		}
		
	}
	private List<KaraokeLine> lines;
	private BufferedImage img;
	private static final int LINES_TO_RENDER = 2;
	private static final int VERTICAL_PADDING = 10;
	private static final int HORIZONTAL_PADDING = 20;
	private static final float STROKE_WIDTH = 3; 
	private Font font = new Font("Verdana", Font.BOLD, 48);
	protected KaraokeScreen() {}
	public static KaraokeScreen getInstance(BufferedImage img, List<KaraokeLine> lyrics) {
		KaraokeScreen s = new KaraokeScreen();
		s.lines = new ArrayList<KaraokeLine>(lyrics);
		s.img = img;
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
		int tw = g.getFontMetrics().stringWidth(text);
		int y = height - (th + VERTICAL_PADDING)*(lineNumber + 1);
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
	
	public BufferedImage render(int lineOffset, int currentLineNum, int lyricOffset) {
		for(int i = 0; i < LINES_TO_RENDER; i++) {
			KaraokeLine kLine = lines.get(lineOffset + (LINES_TO_RENDER - i - 1));
			float scaleFactor = calculateScaleFactor(kLine.toString());
			Point2D offset = calculateOffset(kLine.toString(), i);
			drawText(kLine.toString(), Color.CYAN, scaleFactor, offset);
			if(i == currentLineNum) {
				String partial = kLine.subString(lyricOffset);
				drawText(partial, Color.MAGENTA, scaleFactor, offset);
			}
		}

		return img;
	}
	

	
	
}
