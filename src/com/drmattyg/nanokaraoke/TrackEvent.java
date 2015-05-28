package com.drmattyg.nanokaraoke;

import com.drmattyg.nanokaraoke.MidiFile.VarLength;


public class TrackEvent {

	
	public static class Marker {
		public static int META_MARKER = 0xFF;
		public static int META_TEXT_MARKER = 0x01;
		public static int SYSEX_MARKER = 0xF0;
		public static boolean isMetaMarker(byte b) { return (b & 0xFF) == META_MARKER; }
		public static boolean isTextMarker(byte b) { return (b & 0xFF) == META_TEXT_MARKER; }
		public static boolean isSysexMarker(byte b) { return (b & 0xFF) == SYSEX_MARKER; }
		
	}
	VarLength time;
	enum EventType {
		Meta, Sysex, Midi;
	}
	
	private EventType eventType;
	private int metaType;
	private TrackChunk parent;
	public boolean isMeta() { return eventType == EventType.Meta; } // that's sooooo meta
	public boolean isText() { return isMeta() && metaType == Marker.META_TEXT_MARKER; }
	private TrackEvent() {}
	public static TrackEvent getInstance(TrackChunk tc, int offset) {
		TrackEvent te = new TrackEvent();
		te.setParent(tc);
		int myOffset = offset;
		byte[] b = tc.parent.getBytes();
		te.time = VarLength.read(b, myOffset);
		myOffset += te.time.size;
		if(Marker.isMetaMarker(b[myOffset])) {
			te.eventType = EventType.Meta;
			myOffset++;
			te.metaType = b[myOffset] & 0xFF;
			
			if(Marker.isTextMarker(b[myOffset])) {
				
			}
		} else if(Marker.isSysexMarker(b[myOffset])) {
			te.eventType = EventType.Sysex;
		} else {
			te.eventType = EventType.Midi;
		} 
		return te;
		
	}
	public TrackChunk getParent() {
		return parent;
	}
	public void setParent(TrackChunk parent) {
		this.parent = parent;
	}
	
	
}
