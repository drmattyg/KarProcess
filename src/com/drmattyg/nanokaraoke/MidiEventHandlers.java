package com.drmattyg.nanokaraoke;

import java.util.HashMap;
import java.util.Map;

public class MidiEventHandlers {

	public static final TempoHandler TEMPO_HANDLER = new TempoHandler();
	
	public static class TempoHandler extends MidiEventHandler {
		private Map<Integer, Integer> timeTempoMap = new HashMap<Integer, Integer>();

//		public static TempoHandler getInstance() { return new TempoHandler(); }
		@Override
		public void handleEvent(TrackEvent te) {
			int dataOffset = MetaEventType.TEMPO.dataOffset + te.time.size;
			//needs to know the current actual time!
		}

		
	};
	
	
}
