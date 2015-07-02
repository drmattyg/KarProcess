package com.drmattyg.nanokaraoke;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class MidiEventHandlers {

	public static final TempoHandler TEMPO_HANDLER = new TempoHandler();
	public static final TextHandler TEXT_HANDLER = new TextHandler();
	public static final Collection<MidiEventHandler> DEFAULT_HANDLERS = Arrays.asList(TEMPO_HANDLER, TEXT_HANDLER);
	public static class TempoHandler extends MidiEventHandler {
	private SortedMap<Integer, Integer> timeTempoMap = new TreeMap<Integer, Integer>();

//		public static TempoHandler getInstance() { return new TempoHandler(); }
		@Override
		public void handleEvent(TrackEvent te) {
			if(te.getMetaType() != MetaEventType.TEMPO) return;
			int dataOffset = TrackEvent.getDataOffset(te);
			int timeOffset = te.getTimeOffset();
			int tempo = Utils.toInt(te.getBytes(), 3, dataOffset);
			timeTempoMap.put(timeOffset, tempo);
		}
		public Map<Integer, Integer> getTempoMap() { return timeTempoMap; }
		
		public Integer getInitialTempo() {
			if(timeTempoMap.size() == 0) {
				return 500000;
			} else {
				return timeTempoMap.firstKey();
			}
		}
	}
	
	
	public static class TextHandler extends MidiEventHandler {
		private SortedMap<Integer, TextEvent> lyricsMap = new TreeMap<Integer, TextEvent>();
		@Override
		public void handleEvent(TrackEvent te) {
			if(te.getMetaType() != MetaEventType.TEXT) return;
			lyricsMap.put(te.getTimeOffset(), TextEvent.makeTextEvent(te));
			
		}
		
		public Map<Integer, TextEvent> getTextMap() { return lyricsMap; }
		public List<Integer> getSortedTimeOffsets() { 
			return new ArrayList<Integer>(lyricsMap.keySet());
		}
		
	}
	
	
}
