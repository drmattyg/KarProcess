package com.drmattyg.nanokaraoke.video;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.drmattyg.nanokaraoke.MidiFile;
import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.mediatool.event.VideoPictureEvent;


public class KaraokeScreenMediaTool extends MediaToolAdapter {
	
	private List<KaraokeLine> kLines;
	private KaraokeLine currentLine = null;
	private int currentLineIndex = 0;
	private int currentLyricIndex = 0;
	private long startTime;
	private int lineNum = 0; // 0 = bottom line
	private Entry<Long, String> currentLyric;
	private static final long LYRIC_PRE_START_TIME = 100; // millisecods before the lyric to highlight it
	private static final long LINE_PRE_START_TIME = 1000; // milliseconds before the line to draw it
	protected KaraokeScreenMediaTool() {}
	public static KaraokeScreenMediaTool getInstance(MidiFile mf, long startTime) {
		KaraokeScreenMediaTool k = new KaraokeScreenMediaTool();
		k.kLines = KaraokeLine.toKaraokeLines(mf);
		k.startTime = startTime;
		k.currentLine = k.kLines.get(k.currentLineIndex);
		k.currentLyric = k.currentLine.lyrics.get(k.currentLyricIndex);
		return k;
	}
	
	private Entry<Long, String> nextLyric() {
		if(currentLyricIndex < kLines.get(currentLineIndex).lyrics.size() - 1) {
			return kLines.get(currentLineIndex).lyrics.get(currentLyricIndex + 1);
		}
		if(currentLineIndex < kLines.size() - 1) {
			return kLines.get(currentLineIndex + 1).lyrics.get(0);
		}
		return null;
	}
	
	private boolean isEndOfLine() { 
		return currentLyricIndex == kLines.get(currentLineIndex).lyrics.size() - 1;
	}
	
	@Override
	public void onVideoPicture(IVideoPictureEvent event) {
		long time = event.getTimeStamp(TimeUnit.MILLISECONDS);
		BufferedImage img = event.getImage();
		Entry<Long, String> nextLyric = nextLyric();
		long nextTimePoint = nextLyric != null ? nextLyric.getKey() : Long.MAX_VALUE;
//		KaraokeScreen sc = KaraokeScreen.getInstance(img, kLines);


		if(time > nextTimePoint - LYRIC_PRE_START_TIME) { // 

			if(nextLyric != null) {
				currentLyricIndex++;
			} else if(currentLineIndex < kLines.size() - 1){
				currentLineIndex++;
				currentLyricIndex = 0;
			}
			
		}
//		BufferedImage textImg = sc.render(currentLineIndex, currentLineIndex, currentLyricIndex);
		BufferedImage textImg = null;
		VideoPictureEvent modifiedEvent = new VideoPictureEvent(this, textImg, event.getTimeStamp(), event.getTimeUnit(), event.getStreamIndex());
		super.onVideoPicture(modifiedEvent);
	}
	
	
	
	
	

}
