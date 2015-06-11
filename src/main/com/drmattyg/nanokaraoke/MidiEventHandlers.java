package com.drmattyg.nanokaraoke;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MidiEventHandlers {

	public static final TempoHandler TEMPO_HANDLER = new TempoHandler();
	public static final TextHandler TEXT_HANDLER = new TextHandler();
	public static final Collection<MidiEventHandler> DEFAULT_HANDLERS = Arrays.asList(TEMPO_HANDLER, TEXT_HANDLER);
	public static class TempoHandler extends MidiEventHandler {
		private Map<Integer, Integer> timeTempoMap = new HashMap<Integer, Integer>();

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
		
	}
	
	public static class TextHandler extends MidiEventHandler {
		private Map<Integer, TextEvent> lyricsMap = new HashMap<Integer, TextEvent>();
		@Override
		public void handleEvent(TrackEvent te) {
			if(te.getMetaType() != MetaEventType.TEXT) return;
			lyricsMap.put(te.getTimeOffset(), TextEvent.makeTextEvent(te));
			
		}
		
		public Map<Integer, TextEvent> getTextMap() { return lyricsMap; }
		public List<Integer> getSortedTimeOffsets() { 
			List<Integer> r = new ArrayList<Integer>(lyricsMap.keySet());
			Collections.sort(r);
			return r;
		}
		
	}
	
	
}
