package com.drmattyg.nanokaraoke.video;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.drmattyg.nanokaraoke.MidiFile;
import com.drmattyg.nanokaraoke.TrackChunk;
import com.drmattyg.nanokaraoke.TrackEvent;

public class KaraokeLine implements Iterable<Entry<Long, String>> {
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
	
	private static boolean isLineStart(String s) {
		return s.startsWith("/") || s.startsWith("\\");
	}
	
	public static List<KaraokeLine> toKaraokeLines(MidiFile mf) {
		List<KaraokeLine> lines = new ArrayList<KaraokeLine>();
		KaraokeLine kLine = new KaraokeLine();
		for(TrackChunk tc : mf) {
			for(TrackEvent te : tc) {
				if(te.isText() && te.getTimeOffset() > 0) {
					String text = te.getTextEvent().toString();
					if(isLineStart(text)) {
						if(!kLine.isEmpty()) lines.add(kLine);
						kLine = new KaraokeLine();
					}
					kLine.addLyric(te.getTimeOffset(), text.replaceAll("^[/\\\\]", ""));
				}
			}
		}
		return lines;
	}
	
}