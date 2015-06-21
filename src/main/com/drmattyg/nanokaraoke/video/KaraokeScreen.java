package com.drmattyg.nanokaraoke.video;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
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
	
	private void drawText(String text, int lineNumber, Color fillColor) {
		// draw line number from the bottom (0) up
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
		System.out.println(y);
		System.out.println(height);
		int x = Math.round((width - (tw + 2*HORIZONTAL_PADDING)*sf)/2);
		g.translate(x, y);
		g.scale(sf, sf);
		TextLayout tl = new TextLayout(text, font, g.getFontRenderContext());
		Shape shape = tl.getOutline(null);
		g.setColor(fillColor);
		g.fill(shape);
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(STROKE_WIDTH));
		g.draw(shape);
		
	}
	
	public BufferedImage render(int lineOffset, int currentLineNum, int lyricOffset) {
		KaraokeLine kLine0 = lines.get(lineOffset + 1);
		KaraokeLine kLine1 = lines.get(lineOffset);
		drawText(kLine0.toString(), 0, Color.CYAN);
		drawText(kLine1.toString(), 1, Color.CYAN);
		KaraokeLine currentLine = currentLineNum == 0 ? kLine0 : kLine1;
		drawText(currentLine.subString(lyricOffset), currentLineNum, Color.MAGENTA);
		return img;
	}
	
	public static void test(BufferedImage img) {
		KaraokeScreen s = KaraokeScreen.getInstance(img, new ArrayList<KaraokeLine>());
		s.drawText("I am the very model of a modern major general", 1, Color.CYAN);
		s.drawText("I've information vegetable animal and mineral", 0, Color.CYAN);
	}
	
	
}
