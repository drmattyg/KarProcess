package com.drmattyg.nanokaraoke;

import java.util.HashMap;
import java.util.Map;

public abstract class MidiEventHandler {
	public enum MetaEventType {
		TEXT(0x01, 2), TIME_SIG(0x58, 3), TEMPO(0x51, 3);
		int val;
		int dataOffset;
		private static Map<Integer, MetaEventType> typeMap = new HashMap<Integer, MetaEventType>(MetaEventType.values().length);
		static {
			for(MetaEventType t : MetaEventType.values()) {
				typeMap.put(t.val, t);
			}
		}
		public static MetaEventType getType(int v) { return typeMap.get(v); }
		MetaEventType(int t, int offset) {
			this.val = t;
			this.dataOffset = offset;
		}
		
	}

	public MidiEventHandler() {}
	
	public abstract void handleEvent(TrackEvent te);

}
