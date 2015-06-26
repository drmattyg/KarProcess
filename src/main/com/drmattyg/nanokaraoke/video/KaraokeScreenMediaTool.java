package com.drmattyg.nanokaraoke.video;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.drmattyg.nanokaraoke.MidiEventHandlers;
import com.drmattyg.nanokaraoke.MidiFile;
import com.drmattyg.nanokaraoke.Utils;
import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.mediatool.event.VideoPictureEvent;


public class KaraokeScreenMediaTool extends MediaToolAdapter {
	
	private List<KaraokeLine> kLines;
	private int currentLineIndex = 0;
	private int topLineIndex = 0;
	private int currentLyricIndex = 0;
	private long startTime;
	private MidiFile midiFile;
	private static final long LYRIC_PRE_START_TIME = 100; // millisecods before the lyric to highlight it
	private static final long LINE_PRE_START_TIME = 1000; // milliseconds before the line to draw it
	protected KaraokeScreenMediaTool() {}
	public static KaraokeScreenMediaTool getInstance(MidiFile mf, long startTime) {
		KaraokeScreenMediaTool k = new KaraokeScreenMediaTool();
		k.midiFile = mf;
		k.kLines = KaraokeLine.toKaraokeLines(mf);
		k.startTime = startTime;
		if(MidiEventHandlers.TEMPO_HANDLER.getTempoMap().size() > 1) {
			throw new UnsupportedOperationException("Doesn't support tempo changes (yet)");
		} 
		return k;
	}
	
	private int getTempo() {
		return MidiEventHandlers.TEMPO_HANDLER.getTempoMap().values().iterator().next();
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
		long time = event.getTimeStamp(TimeUnit.MILLISECONDS) - startTime;
		BufferedImage img = event.getImage();
		Entry<Long, String> nextLyric = nextLyric();
		long nextTimePointDelta = nextLyric != null ? nextLyric.getKey() : Long.MAX_VALUE;
		long nextTimePoint = Utils.deltaToMillis(getTempo(), midiFile.getHeaderChunk().getDivision(), nextTimePointDelta);
		KaraokeScreen sc = KaraokeScreen.getInstance(img, kLines, topLineIndex, currentLineIndex, currentLyricIndex);
		BufferedImage textImg = sc.render();
		VideoPictureEvent modifiedEvent = new VideoPictureEvent(this, textImg, event.getTimeStamp(), event.getTimeUnit(), event.getStreamIndex());

		if(time > nextTimePoint - LYRIC_PRE_START_TIME) {
			System.out.println(nextTimePoint);
			if(nextLyric != null) {
				currentLyricIndex++;
			}
			if(isEndOfLine() && currentLineIndex < kLines.size() - 1) {
				currentLineIndex++;
				currentLyricIndex = 0;
				if(currentLineIndex - topLineIndex >=  KaraokeScreen.LINES_TO_RENDER) {
					topLineIndex = currentLineIndex;
				}
			}
		}
			
		super.onVideoPicture(modifiedEvent);
	}
	
	
	
	
	

}
