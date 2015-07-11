package com.drmattyg.nanokaraoke.video;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.drmattyg.nanokaraoke.MidiFile;
import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.mediatool.event.VideoPictureEvent;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;


public class KaraokeScreenMediaTool extends MediaToolAdapter {
	
	private List<KaraokeLine> kLines;
	private int currentLineIndex = 0;
	private int topLineIndex = 0;
	private int currentLyricIndex = -1;
	private long startTime;
	private MidiFile midiFile;
	private IConverter converter = null;
	private static final long LYRIC_PRE_START_TIME = 100; // millisecods before the lyric to highlight it
	private static final long LINE_PRE_START_TIME = 1500; // milliseconds before the line to draw it
	protected KaraokeScreenMediaTool() {}
	public static KaraokeScreenMediaTool getInstance(MidiFile mf, long startTime) {
		KaraokeScreenMediaTool k = new KaraokeScreenMediaTool();
		k.midiFile = mf;
		k.kLines = KaraokeLine.toKaraokeLines(mf);
		k.startTime = startTime;
		return k;
	}
	
//	private int getTempo() {
//		if(mf.getTempoHandler().getTempoMap().size() == 0) return 500000; // this is the default if no tempo is specified
//		return mf.getTempoHandler().getTempoMap().values().iterator().next();
//	}
	
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
		return currentLyricIndex == kLines.get(currentLineIndex).lyrics.size();
	}
	
	@Override
	public void onVideoPicture(IVideoPictureEvent event) {
//		if(zeroTimestamp == 0) {
//			zeroTimestamp = event.getTimeStamp(TimeUnit.MILLISECONDS);
//		}
		long time = event.getTimeStamp(TimeUnit.MILLISECONDS) - startTime; // this works with zero time offset
		BufferedImage img = event.getImage();
		// If the previously chained tool does some manipulation, getImage may return null and we need to convert the image
		if(converter == null) converter = ConverterFactory.createConverter(ConverterFactory.XUGGLER_BGR_24, event.getPicture());
		if(img == null) img = converter.toImage(event.getPicture());
		Entry<Long, String> nextLyric = nextLyric();
		long nextTimePointDelta = nextLyric != null ? nextLyric.getKey() : Long.MAX_VALUE;
//		long nextTimePoint = Utils.deltaToMillis(getTempo(), midiFile.getHeaderChunk().getDivision(), nextTimePointDelta - deltaOffset);
		long nextTimePoint = midiFile.timeOffsetForDelta((int)nextTimePointDelta);
		KaraokeScreen sc = KaraokeScreen.getInstance(img, kLines, topLineIndex, currentLineIndex, currentLyricIndex);
		BufferedImage textImg = sc.render();
		VideoPictureEvent modifiedEvent = new VideoPictureEvent(this, textImg, event.getTimeStamp(), event.getTimeUnit(), event.getStreamIndex());
		if(time > nextTimePoint - LYRIC_PRE_START_TIME) {

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
//			System.out.println(kLines.get(currentLineIndex).lyrics.get(currentLyricIndex) + " L " + currentLineIndex + " Y " + currentLyricIndex);  // for testing
		} 
		// can't get this to work for some reason, punting on this for now.
//		else if(time > nextTimePoint - LINE_PRE_START_TIME && currentLineIndex < kLines.size() - 1 && currentLineIndex - topLineIndex >= KaraokeScreen.LINES_TO_RENDER) {
//			currentLineIndex++;
//			currentLyricIndex = -1;
//			topLineIndex = currentLineIndex;
//		}			
		super.onVideoPicture(modifiedEvent);
	}
	
	
	
	
	

}
